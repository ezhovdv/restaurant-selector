package ru.edv.largecode.restaurant.dto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.edv.largecode.restaurant.dao.Account;

@Data
@EqualsAndHashCode(of = { "username" })
@NoArgsConstructor
public class AccountDto {
	public static AccountDto fromDao(final Account account) {
		final AccountDto dto = new AccountDto();
		dto.setId(account.getId());
		dto.setUsername(account.getUsername());
		dto.setPassword(account.getPassword());
		dto.setVote(VoteDto.fromDao(account.getVote()));
		return dto;
	}

	public static Account toDao(final AccountDto dto) {
		final Account dao = new Account(dto.username, dto.getPassword());
		dao.setId(dto.getId());
		dao.setVote(null);
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
	@JsonView(View.Detail.class)
	private @Nullable VoteDto vote;
}
