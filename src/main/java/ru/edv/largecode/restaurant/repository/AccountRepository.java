package ru.edv.largecode.restaurant.repository;

import java.util.Set;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import ru.edv.largecode.restaurant.dao.Account;

public interface AccountRepository extends Repository<Account, Long> {
	@PreAuthorize(SecurityRules.ROLE_ADMIN)
	void delete(Long id);

//	@PreAuthorize(SecurityRules.ROLE_ADMIN)
//	void deleteAll();

	@PreAuthorize(SecurityRules.ROLE_USER)
	Set<Account> findAll();

//	@PreAuthorize(SecurityRules.ROLE_ADMIN)
	Account findByUsername(String username);

	@PreAuthorize(SecurityRules.ROLE_USER)
	Account findOne(Long id);

	@PreAuthorize(SecurityRules.ROLE_ADMIN)
	Account save(Account account);

	@PreAuthorize(SecurityRules.ROLE_ADMIN)
	Account saveAndFlush(Account account);
}
