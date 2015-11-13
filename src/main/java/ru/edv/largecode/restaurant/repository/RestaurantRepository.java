package ru.edv.largecode.restaurant.repository;

import java.util.Set;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import ru.edv.largecode.restaurant.dao.Restaurant;

public interface RestaurantRepository extends Repository<Restaurant, Long> {
	String RESTAURANT = "restaurant";

	void delete(Restaurant restaurant);

	Set<Restaurant> findAll();

	Restaurant findByName(@Param("name") String name);

	Restaurant findOne(Long id);

	Restaurant save(Restaurant restaurant);

	Restaurant saveAndFlush(Restaurant restaurant);
}
