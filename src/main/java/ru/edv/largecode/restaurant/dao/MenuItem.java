package ru.edv.largecode.restaurant.dao;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "id", "name" })
@ToString(exclude = { "owner" })
@NoArgsConstructor
@Entity
public class MenuItem {
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "menuItemId") Long id;
	@Pattern(regexp = "^[A-Za-z\\s-]+$", message = "Menu item must contain letters, spaces or dashes")
	private @Nonnull @Column(nullable = false, length = 128) String name;
	private @Nonnull @Column(nullable = false) Double price;
	@JoinColumn(name = "restorantId")
	@ManyToOne(optional = false)
	@JsonBackReference
	private Restaurant owner;

	public void setOwner(final Restaurant owner) {
		this.owner = owner;
		final Set<MenuItem> items = owner.getMenuItems();
		if (!items.contains(this)) {
			items.add(this);
		}
	}
}
