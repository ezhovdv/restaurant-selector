package ru.edv.largecode.restaurant.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import ru.edv.largecode.restaurant.RestaurantApplication;
import ru.edv.largecode.restaurant.dto.AccountDto;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { RestaurantApplication.class,
		RestaurantApplication.SecurityConfiguration.class })
@WebIntegrationTest
public class AccountControllerTest {
	private static final String BASE_PATH = "http://localhost:8888/" + AccountController.BASE_PATH;
	private static final String ADMIN = AccountController.ADMIN;

	private static final ParameterizedTypeReference<Set<AccountDto>> ACCOUNT_COLLECTION_TYPE = new ParameterizedTypeReference<Set<AccountDto>>() {
	};

	private final RestTemplate rest = new TestRestTemplate("admin", "admin");

	private AccountDto fetch(final String path) {
		final ResponseEntity<AccountDto> response = rest.getForEntity(path, AccountDto.class);
		assertEquals(response.getStatusCode(), HttpStatus.OK);

		final AccountDto account = response.getBody();
		assertNotNull(account);
		return account;
	}

	private Set<AccountDto> fetchAll(final String path) {
		final ResponseEntity<Set<AccountDto>> response = rest.exchange(path, HttpMethod.GET, null,
				ACCOUNT_COLLECTION_TYPE);
		assertEquals(response.getStatusCode(), HttpStatus.OK);

		final Set<AccountDto> accounts = response.getBody();
		assertFalse(accounts.isEmpty());
		return accounts;
	}

	@Test
	public void findAll() {
		final Set<AccountDto> accounts = fetchAll(BASE_PATH);
		accounts.forEach((item) -> assertNull(item.getPassword()));
	}

	@Test
	public void findAllForAdmin() {
		final Set<AccountDto> accounts = fetchAll(BASE_PATH + ADMIN);
		accounts.forEach((item) -> assertNotNull(item.getPassword()));
	}

	@Test
	public void findById() {
		final AccountDto account = fetch(BASE_PATH + "/1");
		assertNull(account.getPassword());
	}

	@Test
	public void findByIdForAdmin() {
		final AccountDto account = fetch(BASE_PATH + "/1" + ADMIN);
		assertNotNull(account.getPassword());
	}
}
