package ru.edv.largecode.restaurant.repository;

import java.util.Set;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import ru.edv.largecode.restaurant.dao.Restaurant;

public interface RestaurantRepository extends Repository<Restaurant, Long> {
	@PreAuthorize(SecurityRules.ROLE_ADMIN)
	void delete(Long id);

//	@PreAuthorize(SecurityRules.ROLE_ADMIN)
//	void deleteAll();

	@PreAuthorize(SecurityRules.ROLE_USER)
	Set<Restaurant> findAll();

	@PreAuthorize(SecurityRules.ROLE_USER)
	Restaurant findByName(@Param("name") String name);

	@PreAuthorize(SecurityRules.ROLE_USER)
	Restaurant findOne(Long id);

	@PreAuthorize(SecurityRules.ROLE_ADMIN)
	Restaurant save(Restaurant restaurant);

	@PreAuthorize(SecurityRules.ROLE_ADMIN)
	Restaurant saveAndFlush(Restaurant restaurant);
}
