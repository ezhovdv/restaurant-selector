package ru.edv.largecode.restaurant.dto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.edv.largecode.restaurant.dao.Account;
import ru.edv.largecode.restaurant.dao.Vote;

@Data
@EqualsAndHashCode(of = { "username" })
@NoArgsConstructor
public class AccountDto {
	public static AccountDto fromDao(final Account dao) {
		final AccountDto dto = new AccountDto();
		dto.setId(dao.getId());
		dto.setUsername(dao.getUsername());
		dto.setPassword(dao.getPassword());
		dto.setVote(dao.getVote());
		return dto;
	}

	public static Account toDao(final AccountDto dto) {
		final Account dao = new Account(dto.username, dto.getPassword());
		dao.setId(dto.getId());
		dao.setVote(dto.getVote());
		return dao;
	}

	@JsonView(View.Public.class)
	private @Nonnull Long id;
	@JsonView(View.Public.class)
	@Pattern(regexp = "^[A-Za-z\\s-]+$", message = "Username must contain letters, spaces or dashes")
	private @Nonnull String username;
	@JsonView(View.Internal.class)
	@Pattern(regexp = "^[A-Za-z\\s-]+$", message = "Username must contain letters, spaces or dashes")
	private @Nonnull String password;
	@JsonView(View.Internal.class)
	private @Nullable Vote vote;
}
