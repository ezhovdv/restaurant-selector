package ru.edv.largecode.restaurant.repository;

import java.util.Set;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import ru.edv.largecode.restaurant.dao.Account;

public interface AccountRepository extends Repository<Account, Long> {
	String ACCOUNT = "account";

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Set<Account> findAll();

	Account findByUsername(String username);

	Account findOne(Long id);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Account save(Account account);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Account saveAndFlush(Account account);
}
