package ru.edv.largecode.restaurant.repository;

import java.util.Set;

import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import ru.edv.largecode.restaurant.dao.Vote;

public interface VoteRepository extends Repository<Vote, Long> {
	@PreAuthorize(SecurityRules.ROLE_USER)
	Set<Vote> findAll();

	@PreAuthorize(SecurityRules.ROLE_USER)
	Vote save(Vote vote);

	@PreAuthorize(SecurityRules.ROLE_USER)
	Vote saveAndFlush(Vote vote);
}
