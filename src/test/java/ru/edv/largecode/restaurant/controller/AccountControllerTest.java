package ru.edv.largecode.restaurant.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import ru.edv.largecode.restaurant.RestaurantApplication;
import ru.edv.largecode.restaurant.dao.Account;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { RestaurantApplication.class,
		RestaurantApplication.SecurityConfiguration.class })
@WebIntegrationTest()
public class AccountControllerTest {
	private static final String BASE_URI = "http://localhost:8888/api/v1/";
	private static final String BASE_PATH = BASE_URI + RestaurantController.BASE_PATH;
	private final RestTemplate rest = new TestRestTemplate();

	@Test
	public void findByUsernameTest() {
		final ResponseEntity<Account> entity = rest.getForEntity(BASE_PATH + "/search/findByName/?name=Bogus",
				Account.class);
		final Account account = entity.getBody();
		System.out.println(account);
	}
}
