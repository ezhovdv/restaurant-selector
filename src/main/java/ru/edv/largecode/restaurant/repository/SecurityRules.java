package ru.edv.largecode.restaurant.repository;

public interface SecurityRules {
	String ROLE_USER = "hasRole('ROLE_USER')";
	String ROLE_ADMIN = "hasRole('ROLE_ADMIN')";
}
