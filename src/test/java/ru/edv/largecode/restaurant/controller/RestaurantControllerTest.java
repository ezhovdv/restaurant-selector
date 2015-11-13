package ru.edv.largecode.restaurant.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import ru.edv.largecode.restaurant.dto.RestaurantDto;
import ru.edv.largecode.restaurant.error.ErrorDetail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { RestaurantApplication.class,
		RestaurantApplication.SecurityConfiguration.class })
@WebIntegrationTest
public class RestaurantControllerTest {
	private static final String BASE_PATH = "http://localhost:8888/" + RestaurantController.BASE_PATH;
	private static final String DETAILS = RestaurantController.DETAILS;

	private static final ParameterizedTypeReference<Set<RestaurantDto>> RESTORAUNT_COLLECTION_TYPE = new ParameterizedTypeReference<Set<RestaurantDto>>() {
	};

	private final RestTemplate rest = new TestRestTemplate("admin", "admin");

	private RestaurantDto fetch(final String path) {
		final ResponseEntity<RestaurantDto> response = rest.getForEntity(path, RestaurantDto.class);
		assertEquals(response.getStatusCode(), HttpStatus.OK);

		final RestaurantDto item = response.getBody();
		assertNotNull(item);
		return item;
	}

	private Set<RestaurantDto> fetchAll(final String path) {
		final ResponseEntity<Set<RestaurantDto>> response = rest.exchange(path, HttpMethod.GET, null,
				RESTORAUNT_COLLECTION_TYPE);
		assertEquals(response.getStatusCode(), HttpStatus.OK);

		final Set<RestaurantDto> items = response.getBody();
		assertFalse(items.isEmpty());
		return items;
	}

	@Test
	public void findAll() {
		final Set<RestaurantDto> items = fetchAll(BASE_PATH);
		items.forEach((item) -> assertTrue(item.getVotes().isEmpty()));
	}

	@Test
	public void findAllWithDetails() {
		final Set<RestaurantDto> items = fetchAll(BASE_PATH + DETAILS);
		assertTrue(items.stream().filter(dto -> !dto.getVotes().isEmpty()).count() > 0);
	}

	@Test
	public void findById() {
		final RestaurantDto item = fetch(BASE_PATH + "/1");
		assertTrue(item.getVotes().isEmpty());
	}

	@Test
	public void findByIdWithDetails() {
		final RestaurantDto item = fetch(BASE_PATH + "/1" + DETAILS);
		assertTrue(!item.getVotes().isEmpty());
	}

	@Test
	public void findByWrongId() {
		final String path = BASE_PATH + "/500";
		final ResponseEntity<ErrorDetail> response = rest.getForEntity(path, ErrorDetail.class);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

		final ErrorDetail error = response.getBody();
		assertNotNull(error);
	}
}
