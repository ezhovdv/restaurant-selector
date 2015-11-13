package ru.edv.largecode.restaurant.dto;

import javax.annotation.Nonnull;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edv.largecode.restaurant.dao.Vote;

@Data
@NoArgsConstructor
public class VoteDto {
	public static VoteDto fromDao(final Vote vote) {
		final VoteDto dto = new VoteDto();
		dto.setAccountId(vote.getAccount().getId());
		dto.setRestaurantId(vote.getRestaurant().getId());
		return dto;
	}

	private @Nonnull Long accountId;
	private @Nonnull Long restaurantId;
}
