package ru.edv.largecode.restaurant.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import ru.edv.largecode.restaurant.dao.Restaurant;
import ru.edv.largecode.restaurant.dto.RestaurantDto;
import ru.edv.largecode.restaurant.dto.View;
import ru.edv.largecode.restaurant.error.ErrorDetail;
import ru.edv.largecode.restaurant.repository.RestaurantRepository;

@RestController
@RequestMapping("api/v1/restaurant")
public class RestaurantController {
	private final RestaurantRepository repo;

	@Autowired
	public RestaurantController(final RestaurantRepository repo) {
		this.repo = repo;
	}

	@RequestMapping(method = RequestMethod.POST)
	@JsonView(View.Internal.class)
	public RestaurantDto create(@RequestBody final RestaurantDto dto) {
		final Restaurant restaurant = RestaurantDto.toDao(dto);
		final Restaurant dao = repo.saveAndFlush(restaurant);
		return RestaurantDto.fromDao(dao);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ErrorDetail errorCVE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setDescription(ErrorDetail.CONSTRAINT_VIOLATION);
		error.setMessage(exception.getLocalizedMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorDetail errorDIVE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setDescription(ErrorDetail.INTEGRITY_VIOLATION);
		error.setMessage(exception.getLocalizedMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

	@RequestMapping(method = RequestMethod.GET)
	@JsonView(View.Internal.class)
	public Set<RestaurantDto> findAll() {
		final Set<Restaurant> daos = repo.findAll();
		final Set<RestaurantDto> dtos = new HashSet<>(daos.size());
		daos.forEach((item) -> {
			dtos.add(RestaurantDto.fromDao(item));
		});
		return dtos;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@JsonView(View.Public.class)
	public RestaurantDto findOne(@PathVariable final Long id) {
		final Restaurant restaurant = repo.findOne(id);
		return RestaurantDto.fromDao(restaurant);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@JsonView(View.Internal.class)
	public RestaurantDto save(@RequestBody final RestaurantDto dto, @PathVariable final Long id) {
		final Restaurant restoraunt = RestaurantDto.toDao(dto);
		restoraunt.setId(id);
		final Restaurant dao = repo.save(restoraunt);
		return RestaurantDto.fromDao(dao);
	}
}
