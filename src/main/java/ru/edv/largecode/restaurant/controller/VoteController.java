package ru.edv.largecode.restaurant.controller;

import java.time.LocalTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ru.edv.largecode.restaurant.dao.Account;
import ru.edv.largecode.restaurant.dao.Restaurant;
import ru.edv.largecode.restaurant.dao.Vote;
import ru.edv.largecode.restaurant.dto.VoteDto;
import ru.edv.largecode.restaurant.error.ErrorDetail;
import ru.edv.largecode.restaurant.repository.AccountRepository;
import ru.edv.largecode.restaurant.repository.RestaurantRepository;
import ru.edv.largecode.restaurant.repository.VoteRepository;

@RestController
@RequestMapping(VoteController.PASE_PATH)
public class VoteController {
	static final String PASE_PATH = "api/v1/vote";
	private final VoteRepository repo;
	private final RestaurantRepository restRepo;
	private final AccountRepository accRepo;

	@Autowired
	public VoteController(final VoteRepository repo, final RestaurantRepository restRepo,
			final AccountRepository accRepo) {
		this.repo = repo;
		this.restRepo = restRepo;
		this.accRepo = accRepo;
	}

	private boolean canUpdate() {
		final LocalTime lt = LocalTime.now();
		final LocalTime lt11 = LocalTime.of(11, 0);
		return lt.isBefore(lt11);
	}

	@ExceptionHandler(NullPointerException.class)
	public ErrorDetail errorNPE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setDescription(ErrorDetail.EMPTY_REQUEST_DATA);
		error.setMessage(exception.getLocalizedMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

	@RequestMapping(method = RequestMethod.POST)
	public VoteDto save(@RequestBody final VoteDto dto) {
		final Account account = accRepo.findOne(dto.getAccountId());
		final Restaurant restaurant = restRepo.findOne(dto.getRestaurantId());

		final Vote vote = new Vote();
		vote.setAccount(account);
		vote.setRestaurant(restaurant);

		final Vote oldVote = account.getVote();
		if (null != oldVote) {
			if (canUpdate()) {
				vote.setId(oldVote.getId());
			} else {
				return VoteDto.fromDao(oldVote);
			}
		}
		final Vote dao = repo.saveAndFlush(vote);
		return VoteDto.fromDao(dao);
	}
}
