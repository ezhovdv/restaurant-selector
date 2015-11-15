package ru.edv.largecode.restaurant.dto;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edv.largecode.restaurant.dao.Vote;

@Data
@NoArgsConstructor
public class VoteDto {
	public static VoteDto fromDao(final Vote dao) {
		if (null != dao) {
			final VoteDto dto = new VoteDto();
			dto.setAccountId(dao.getAccount().getId());
			dto.setRestaurantId(dao.getRestaurant().getId());
			return dto;
		} else {
			return null;
		}
	}

	@JsonView(View.Public.class)
	private @Nonnull Long accountId;
	@JsonView(View.Public.class)
	private @Nonnull Long restaurantId;
}
