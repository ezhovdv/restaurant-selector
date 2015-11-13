package ru.edv.largecode.restaurant.dao;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(exclude = { "vote" })
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Account {
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "accountId") Long id;
	private @Nonnull @Column(nullable = false, length = 32, unique = true) String username;
	private @Nonnull @Column(nullable = false, length = 64) String password;

	@OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Vote vote;
}
