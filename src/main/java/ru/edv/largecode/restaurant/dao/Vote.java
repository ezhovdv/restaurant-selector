package ru.edv.largecode.restaurant.dao;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = { "account", "restaurant" })
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Vote {
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	private @Nonnull @JoinColumn(name = "accountId") @OneToOne(fetch = FetchType.LAZY, optional = false) Account account;
	private @Nonnull @JoinColumn(name = "restaurantId") @ManyToOne(optional = false) Restaurant restaurant;
//	private @Temporal(TemporalType.TIMESTAMP) Date date = new Date();
}
