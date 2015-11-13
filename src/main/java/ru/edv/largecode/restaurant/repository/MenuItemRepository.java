package ru.edv.largecode.restaurant.repository;

import java.util.Set;

import ru.edv.largecode.restaurant.dao.MenuItem;

public interface MenuItemRepository {
//	extends Repository<MenuItem, Long>
	String MENU = "menu";

	Set<MenuItem> findAll();

}
