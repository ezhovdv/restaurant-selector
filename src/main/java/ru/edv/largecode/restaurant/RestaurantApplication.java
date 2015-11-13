package ru.edv.largecode.restaurant;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Priority;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.edv.largecode.restaurant.dao.Account;
import ru.edv.largecode.restaurant.repository.AccountRepository;

@SpringBootApplication
@Slf4j
public class RestaurantApplication {
	@Service
	@Slf4j
	protected static class AccountService implements UserDetailsService {
		@Autowired
		private AccountRepository repo;

		private Collection<? extends GrantedAuthority> getGrantedAuthorities(final String username) {
			Collection<? extends GrantedAuthority> authorities;
			if (username.equals("admin")) {
				authorities = Arrays.asList(() -> "ROLE_ADMIN", () -> "ROLE_USER");
			} else {
				authorities = Arrays.asList(() -> "ROLE_USER");
			}
			log.debug("User [{}] has been granted authorities [{}]", username, authorities);
			return authorities;
		}

		@Override
		public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
			final Account account = repo.findByUsername(username);
			if (null != account) {
				return new User(username, account.getPassword(), getGrantedAuthorities(username));
			} else {
				throw new UsernameNotFoundException("Username " + username + " not found");
			}
		}
	}

	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@EnableWebSecurity
	@Priority(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	@Slf4j
	public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Autowired
		private AccountService service;

		@Override
		protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(service);
		}

		@Override
		protected void configure(final HttpSecurity http) throws Exception {
//			http.httpBasic().and().authorizeRequests().anyRequest().authenticated();
			http.authorizeRequests().anyRequest().fullyAuthenticated();
			http.httpBasic();
			http.csrf().disable();
			enableH2ConsoleFrames(http);
			log.debug("Security has been configured");
		}

		private void enableH2ConsoleFrames(final HttpSecurity http) throws Exception {
			http.headers().addHeaderWriter(
					new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))));
		}

	}

	public static void main(final String[] args) {
		SpringApplication.run(RestaurantApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean h2servletRegistration() {
		final ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
		registration.addUrlMappings("/console/*");
		log.debug("H2 servlet registration is ready");
		return registration;
	}

}
