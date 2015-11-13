package ru.edv.largecode.restaurant.dao;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = { "menuItems", "votes" })
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Restaurant {
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "restaurantId") Long id;
	private @Nonnull @Column(nullable = false, length = 128, unique = true) String name;

	private @Nonnull @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) Set<MenuItem> menuItems = new HashSet<>();
	private @OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) List<Vote> votes = new LinkedList<>();

	public Restaurant(final String name, final Set<MenuItem> menuItems) {
		this.name = name;
		this.menuItems = menuItems;
		menuItems.forEach((mi) -> {
			if (mi.getOwner() != this) {
				mi.setOwner(this);
			}
		});
	}

	void setMenuItems(final Set<MenuItem> menuItems) {
		this.menuItems.clear();
		menuItems.forEach((mi) -> {
			if (this != mi.getOwner()) {
				mi.setOwner(this);
			}
		});
	}
}
