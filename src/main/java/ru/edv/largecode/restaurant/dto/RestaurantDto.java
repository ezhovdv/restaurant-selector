package ru.edv.largecode.restaurant.dto;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.edv.largecode.restaurant.dao.MenuItem;
import ru.edv.largecode.restaurant.dao.Restaurant;

@Data
@EqualsAndHashCode(of = { "id", "name" })
@NoArgsConstructor
public class RestaurantDto {
	public static RestaurantDto fromDao(@Nonnull final Restaurant dao) {
		final RestaurantDto dto = new RestaurantDto();
		dto.setId(dao.getId());
		dto.setName(dao.getName());
		final Set<MenuItemDto> items = dto.getItems();
		dao.getMenuItems().forEach((menuItem) -> items.add(MenuItemDto.fromDao(menuItem)));

		final Set<String> votes = dto.getVotes();
		dao.getVotes().forEach((item) -> {
			votes.add(item.getAccount().getUsername());
		});

		return dto;
	}

	public static Restaurant toDao(@Nonnull final RestaurantDto dto) {
		final Restaurant dao = new Restaurant();
		dao.setName(dto.getName());
		final Set<MenuItem> items = dao.getMenuItems();
		dto.items.forEach((item) -> {
			final MenuItem menuItem = MenuItemDto.toDao(item);
			menuItem.setOwner(dao);
			items.add(menuItem);
		});
		return dao;
	}

	@JsonView(View.Public.class)
	private @Nonnull Long id;
	@JsonView(View.Public.class)
	private @Nonnull String name;
	@JsonView(View.Public.class)
	private Set<MenuItemDto> items = new HashSet<>();
	@JsonView(View.Internal.class)
	private Set<String> votes = new HashSet<>();
}
