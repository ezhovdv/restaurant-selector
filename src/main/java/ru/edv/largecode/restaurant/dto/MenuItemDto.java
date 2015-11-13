package ru.edv.largecode.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import ru.edv.largecode.restaurant.dao.MenuItem;

@Data
public class MenuItemDto {
	public static MenuItemDto fromDao(final MenuItem menuItem) {
		final MenuItemDto dto = new MenuItemDto();
		dto.setName(menuItem.getName());
		dto.setPrice(menuItem.getPrice());
		return dto;
	}

	public static MenuItem toDao(final MenuItemDto dto) {
		final MenuItem dao = new MenuItem();
		dao.setName(dto.getName());
		dao.setPrice(dto.getPrice());
		return dao;
	}

	@JsonView(View.Public.class)
	private String name;
	@JsonView(View.Public.class)
	private Double price;
}
