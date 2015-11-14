package ru.edv.largecode.restaurant;

import static com.google.common.collect.Lists.newArrayList;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Priority;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.classmate.TypeResolver;

import lombok.extern.slf4j.Slf4j;
import ru.edv.largecode.restaurant.dao.Account;
import ru.edv.largecode.restaurant.repository.AccountRepository;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
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
				throw new UsernameNotFoundException("AUTHORIZATION failed: Username " + username + " not found");
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

	private static final String API_ROOT_REGEX = "/api/v1/\\w+(/[\\w,!]+)*";

	public static void main(final String[] args) {
		SpringApplication.run(RestaurantApplication.class, args);
	}

	@Autowired
	private TypeResolver typeResolver;

//	private ApiKey apiKey() {
//		return new ApiKey("mykey", "api_key", "header");
//	}

	List<SecurityReference> defaultAuth() {
		final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return newArrayList(new SecurityReference("mykey", authorizationScopes));
	}

	@Bean
	public ServletRegistrationBean h2servletRegistration() {
		final ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
		registration.addUrlMappings("/console/*");
		log.debug("H2 servlet registration is ready");
		return registration;
	}

	@Bean
	public Docket restaurantSelectorApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex(API_ROOT_REGEX)).build().pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class).genericModelSubstitutes(ResponseEntity.class)
				//				.alternateTypeRules(newRule(
				//						typeResolver.resolve(DeferredResult.class,
				//								typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
				//						typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, newArrayList(new ResponseMessageBuilder().code(500)
						.message("500 message").responseModel(new ModelRef("Error")).build()))
//				.securitySchemes(newArrayList(apiKey())).securityContexts(newArrayList(securityContext()))
						;
	}

//	private SecurityContext securityContext() {
//		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/anyPath.*"))
//				.build();
//	}
}
