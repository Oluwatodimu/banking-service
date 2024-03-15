package com.woodcore.backend.bankingservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woodcore.backend.bankingservice.security.filter.CsrfCookieFilter;
import com.woodcore.backend.bankingservice.security.filter.JwtValidationFilter;
import com.woodcore.backend.bankingservice.security.filter.RateLimiterFilter;
import com.woodcore.backend.bankingservice.security.jwt.JwtTokenProvider;
import com.woodcore.backend.bankingservice.utils.AuthoritiesConstants;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final RateLimiter rateLimiter;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        CsrfTokenRequestAttributeHandler requestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestAttributeHandler)
                        .ignoringRequestMatchers("/api/v1/users/register")
                        .ignoringRequestMatchers("/api/v1/users/login")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .cors().configurationSource(
                        request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(List.of("*")); // update with list of frontend urls
                            configuration.setAllowedMethods(Collections.singletonList("*"));
                            configuration.setAllowCredentials(true);
                            configuration.setAllowedHeaders(Collections.singletonList("*"));
                            configuration.setExposedHeaders(List.of(AuthoritiesConstants.AUTHORITIES_HEADER));
                            configuration.setMaxAge(3600L);
                            return configuration;
                        })
                .and()
                .addFilterBefore(new JwtValidationFilter(jwtTokenProvider), BasicAuthenticationFilter.class)
                .addFilterAfter(new RateLimiterFilter(rateLimiter, objectMapper), WebAsyncManagerIntegrationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/v1/users/register").permitAll()
                .requestMatchers("/api/v1/users/login").permitAll()
                .requestMatchers("/api/v1/accounts").authenticated()
                .requestMatchers("/api/v1/accounts/**").authenticated()
                .requestMatchers("/api/v1/transactions/deposit").permitAll()
                .requestMatchers("/api/v1/transactions/withdraw").authenticated()
                .requestMatchers("/api/v1/transactions/transfer").authenticated()
                .requestMatchers("/api/v1/transactions").authenticated()
                .requestMatchers("/api/v1/transactions/type").authenticated()
        ;

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
