package com.woodcore.backend.bankingservice;

import com.woodcore.backend.bankingservice.model.Authority;
import com.woodcore.backend.bankingservice.model.Currency;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.model.enums.CurrencySymbol;
import com.woodcore.backend.bankingservice.model.enums.UserType;
import com.woodcore.backend.bankingservice.repository.AuthorityRepository;
import com.woodcore.backend.bankingservice.repository.CurrencyRepository;
import com.woodcore.backend.bankingservice.repository.UserRepository;
import com.woodcore.backend.bankingservice.utils.AuthoritiesConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;


@SpringBootApplication
public class BankingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner createUserRoles(AuthorityRepository authorityRepository) {
		return (args -> {
			List<Authority> authorities = authorityRepository.findAll();
			if (authorities.isEmpty()) {
				Authority userAuthority = new Authority();
				userAuthority.setId(UUID.randomUUID());
				userAuthority.setAuthorityName(AuthoritiesConstants.USER);

				Authority adminAuthority = new Authority();
				adminAuthority.setId(UUID.randomUUID());
				adminAuthority.setAuthorityName(AuthoritiesConstants.ADMIN);

				authorityRepository.save(userAuthority);
				authorityRepository.save(adminAuthority);
			}
		});
	}

	@Bean
	@Profile(value = "!" + "prod")
	public CommandLineRunner createAdminUser(UserRepository userRepository, AuthorityRepository authorityRepository,
											 PasswordEncoder passwordEncoder) {
		return (args -> {
			Optional<User> adminUserOptional = userRepository.findByEmailIgnoreCase("admin@gmail.com");
			if (adminUserOptional.isEmpty()) {
				User user = User.builder()
						.email("admin@gmail.com")
						.password(passwordEncoder.encode("password123$"))
						.userType(UserType.ADMIN)
						.activated(true)
						.build();

				Set<Authority> authorities = new HashSet<>();
				authorityRepository.findByAuthorityName(AuthoritiesConstants.ADMIN).ifPresent(authorities::add);
				user.setAuthorities(authorities);
				userRepository.save(user);
			}
		});
	}

	@Bean
	public CommandLineRunner createCurrencies(CurrencyRepository currencyRepository) {
		return (args -> {
			List<Currency> currencies = currencyRepository.findAll();
			if (currencies.isEmpty()) {
				Currency naira = new Currency();
				naira.setId(UUID.randomUUID());
				naira.setName("naira");
				naira.setSymbol(CurrencySymbol.NGN);
				naira.setEnabled(true);

				Currency dollar = new Currency();
				dollar.setId(UUID.randomUUID());
				dollar.setName("dollar");
				dollar.setSymbol(CurrencySymbol.USD);
				dollar.setEnabled(true);

				currencyRepository.save(naira);
				currencyRepository.save(dollar);
			}
		});
	}
}
