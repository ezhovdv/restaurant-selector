package ru.edv.largecode.restaurant.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import ru.edv.largecode.restaurant.dao.Account;
import ru.edv.largecode.restaurant.dto.AccountDto;
import ru.edv.largecode.restaurant.dto.View;
import ru.edv.largecode.restaurant.repository.AccountRepository;

@RestController
@RequestMapping(AccountController.BASE_PATH)
public class AccountController {
	static final String BASE_PATH = "api/v1/account";
	private final AccountRepository repo;

	@Autowired
	public AccountController(final AccountRepository repo) {
		this.repo = repo;
	}

	@JsonView(View.Public.class)
	@RequestMapping(method = RequestMethod.GET)
	public Set<AccountDto> findAll() {
		final Set<Account> daos = repo.findAll();
		final Set<AccountDto> dtos = new HashSet<>();
		daos.forEach((item) -> {
			dtos.add(AccountDto.fromDao(item));
		});
		return dtos;
	}

	@JsonView(View.Public.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public AccountDto findOne(@PathVariable final Long id) {
		final Account dao = repo.findOne(id);
		return AccountDto.fromDao(dao);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public AccountDto save(@RequestBody final AccountDto dto, @PathVariable final Long id) {
		final Account account = AccountDto.toDao(dto);
		account.setId(id);
		final Account dao = repo.save(account);
		return AccountDto.fromDao(dao);
	}
}
