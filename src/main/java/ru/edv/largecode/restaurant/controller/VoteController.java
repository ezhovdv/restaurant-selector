package ru.edv.largecode.restaurant.controller;

import java.time.LocalTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class TooLateException extends RuntimeException {
		private static final long serialVersionUID = -544071656018964530L;
		private final Long accountId;
		private final Long restaurantId;

		@Override
		public String getMessage() {
			return "AccountId: " + accountId + ", restaurantId: " + restaurantId;
		}
	}

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

	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = ErrorDetail.NOT_FOUND)
	@ExceptionHandler(NullPointerException.class)
	public ErrorDetail errorNPE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setDescription(ErrorDetail.NOT_FOUND);
		error.setMessage(exception.getLocalizedMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

	@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
	@ExceptionHandler(TooLateException.class)
	public ErrorDetail errorTLE(final HttpServletRequest request, final Exception exception) {
		final ErrorDetail error = new ErrorDetail();
		error.setStatus(HttpStatus.EXPECTATION_FAILED.value());
		error.setDescription("Forbidden: It's too late to change your mind (more then 11:00)");
		error.setMessage(exception.getMessage());
		error.setUrl(request.getRequestURL().toString());
		return error;
	}

	@RequestMapping(method = RequestMethod.POST)
	public VoteDto save(@RequestBody final VoteDto dto) {
		final Long accountId = dto.getAccountId();
		final Account account = accRepo.findOne(accountId);
		final Long restaurantId = dto.getRestaurantId();
		final Restaurant restaurant = restRepo.findOne(restaurantId);

		final Vote vote = new Vote();
		vote.setAccount(account);
		vote.setRestaurant(restaurant);

		final Vote oldVote = account.getVote();
		if (null != oldVote) {
			if (canUpdate()) {
				vote.setId(oldVote.getId());
			} else {
				throw new TooLateException(accountId, restaurantId);
//				return VoteDto.fromDao(oldVote);
			}
		}
		final Vote dao = repo.saveAndFlush(vote);
		return VoteDto.fromDao(dao);
	}
}
