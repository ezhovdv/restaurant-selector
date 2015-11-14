package ru.edv.largecode.restaurant.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import ru.edv.largecode.restaurant.dao.Account;
import ru.edv.largecode.restaurant.dto.AccountDto;
import ru.edv.largecode.restaurant.dto.View;
import ru.edv.largecode.restaurant.error.ErrorDetail;
import ru.edv.largecode.restaurant.repository.AccountRepository;

@RestController
@RequestMapping(AccountController.BASE_PATH)
public class AccountController {
	static final String ADMIN = "/admin!";
	static final String BASE_PATH = "api/v1/account";
	private final AccountRepository repo;

	@Autowired
	public AccountController(final AccountRepository repo) {
		this.repo = repo;
	}

	@JsonView(View.Public.class)
	@RequestMapping(method = RequestMethod.POST)
	public AccountDto create(@RequestBody final AccountDto dto) {
		final Account restaurant = AccountDto.toDao(dto);
		final Account dao = repo.saveAndFlush(restaurant);
		return AccountDto.fromDao(dao);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	void deleteById(@PathVariable final Long id) {
		repo.delete(id);
	}

	@ResponseStatus(value = HttpStatus.CONFLICT, reason = ErrorDetail.CONSTRAINT_VIOLATION)
	@ExceptionHandler(ConstraintViolationException.class)
	public ErrorDetail errorCVE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setDescription(ErrorDetail.CONSTRAINT_VIOLATION);
		error.setMessage(exception.getLocalizedMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

	@ResponseStatus(value = HttpStatus.CONFLICT, reason = ErrorDetail.INTEGRITY_VIOLATION)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorDetail errorDIVE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setDescription(ErrorDetail.INTEGRITY_VIOLATION);
		error.setMessage(exception.getLocalizedMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

//	@RequestMapping(value = "all", method = RequestMethod.DELETE)
//	void deleteAll() {
//		repo.deleteAll();
//	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = ErrorDetail.NOT_FOUND)
	@ExceptionHandler(NullPointerException.class)
	public ErrorDetail errorNPE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setDescription(ErrorDetail.NOT_FOUND);
		error.setMessage(exception.getLocalizedMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

	@JsonView(View.Public.class)
	@RequestMapping(method = RequestMethod.GET)
	public Set<AccountDto> findAll() {
		return selectAllAccounts();
	}

	@JsonView(View.Internal.class)
	@RequestMapping(value = ADMIN, method = RequestMethod.GET)
	public Set<AccountDto> findAllForAdmin() {
		return selectAllAccounts();
	}

	@JsonView(View.Public.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public AccountDto findById(@PathVariable final Long id) {
		final Account dao = repo.findOne(id);
		return AccountDto.fromDao(dao);
	}

	@JsonView(View.Internal.class)
	@RequestMapping(value = "/{id}" + ADMIN, method = RequestMethod.GET)
	public AccountDto findByIdForAdmin(@PathVariable final Long id) {
		final Account dao = repo.findOne(id);
		return AccountDto.fromDao(dao);
	}

	@JsonView(View.Internal.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public AccountDto save(@RequestBody final AccountDto dto, @PathVariable final Long id) {
		final Account account = AccountDto.toDao(dto);
		account.setId(id);
		final Account dao = repo.save(account);
		return AccountDto.fromDao(dao);
	}

	private Set<AccountDto> selectAllAccounts() {
		final Set<Account> daos = repo.findAll();
		final Set<AccountDto> dtos = new HashSet<>();
		daos.forEach((item) -> {
			dtos.add(AccountDto.fromDao(item));
		});
		return dtos;
	}
}
