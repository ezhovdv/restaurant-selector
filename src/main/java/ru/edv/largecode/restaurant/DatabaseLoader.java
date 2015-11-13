package ru.edv.largecode.restaurant;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import ru.edv.largecode.restaurant.dao.Account;
import ru.edv.largecode.restaurant.dao.MenuItem;
import ru.edv.largecode.restaurant.dao.Restaurant;
import ru.edv.largecode.restaurant.dao.Vote;
import ru.edv.largecode.restaurant.repository.AccountRepository;
import ru.edv.largecode.restaurant.repository.RestaurantRepository;
import ru.edv.largecode.restaurant.repository.VoteRepository;

@Component
@Slf4j
public class DatabaseLoader implements CommandLineRunner {
	private final RestaurantRepository restaurantRep;
	private final AccountRepository accountRep;
	private final VoteRepository voteRep;

	@Autowired
	public DatabaseLoader(final RestaurantRepository menuRepository, final AccountRepository accountRepository,
			final VoteRepository voteRepository) {
		restaurantRep = menuRepository;
		accountRep = accountRepository;
		voteRep = voteRepository;
	}

	private void fillAccounts() {
		accountRep.saveAndFlush(new Account("admin", "admin"));
		accountRep.saveAndFlush(new Account("zoo", "park"));
		accountRep.saveAndFlush(new Account("Mark", "Aurelius"));
		accountRep.saveAndFlush(new Account("Juilus", "Caesar"));
		accountRep.saveAndFlush(new Account("Beavis", "The First"));
		accountRep.saveAndFlush(new Account("Butthead", "The Second"));
	}

	private void fillRestorants() {
		restaurantRep.saveAndFlush(new Restaurant("Bogus",
				Sets.newHashSet(new MenuItem("Butterfly Wings", 1.75), new MenuItem("Rose petals", 2.25))));
		restaurantRep.saveAndFlush(new Restaurant("Marius",
				Sets.newHashSet(new MenuItem("Fish and chips", 0.75), new MenuItem("Cola-Cola", 0.25))));
		restaurantRep.saveAndFlush(new Restaurant("Neptune",
				Sets.newHashSet(new MenuItem("Shark and oysters", 2.75), new MenuItem("White Wine", 2.25))));
	}

	private void fillVotes() {
		final Restaurant r1 = restaurantRep.findOne(1L);
		final Account acc1 = accountRep.findOne(1L);
		final Account acc2 = accountRep.findOne(2L);
		if (null != r1 && null != acc1) {
			voteRep.saveAndFlush(new Vote(acc1, r1));
			voteRep.saveAndFlush(new Vote(acc2, r1));
		}

	}

	@Override
	public void run(final String... args) throws Exception {
		log.debug("Filling data");

		final SecurityContext ctx = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext(ctx);
		ctx.setAuthentication(new UsernamePasswordAuthenticationToken("1st", "2nd",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))));
		fillRestorants();
		log.debug("Restaurants have been loaded");
		fillAccounts();
		log.debug("Accounts have been loaded ");
		fillVotes();
		log.debug("Votes has been loaded");
	}
}
