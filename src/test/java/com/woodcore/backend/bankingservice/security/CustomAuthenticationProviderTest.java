package com.woodcore.backend.bankingservice.security;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationProviderTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private CustomAuthenticationProvider customAuthenticationProvider;

    UsernamePasswordAuthenticationToken authenticationToken;
    User securityUser;

    @BeforeEach
    public void init() {
        authenticationToken = new UsernamePasswordAuthenticationToken(
                "test-user",
                "test-password-12345"
        );

        securityUser = new User(
                (String) authenticationToken.getPrincipal(),
                (String) authenticationToken.getCredentials(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @SneakyThrows
    @DisplayName("complete user auth with matching passwords")
    public void authenticationWhenPasswordsMatch() {

        given(customUserDetailsService.loadUserByUsername(any(String.class))).willReturn(securityUser);
        given(passwordEncoder.matches(authenticationToken.getCredentials().toString(), securityUser.getPassword())).willReturn(true);
        Authentication authentication = customAuthenticationProvider.authenticate(authenticationToken);

        assertThat(authentication).isNotNull();
        assertThat(authentication.getCredentials()).isNotNull();
        assertThat(authentication.getAuthorities()).isNotNull();
    }

    @Test
    @DisplayName("ensure authentication fails with mismatched passwords ")
    public void authenticationWhenPasswordsMismatch() {
        given(customUserDetailsService.loadUserByUsername(any(String.class))).willReturn(securityUser);
        given(passwordEncoder.matches(authenticationToken.getCredentials().toString(), securityUser.getPassword())).willReturn(false);
        Assertions.assertThrows(RuntimeException.class, () -> customAuthenticationProvider.authenticate(authenticationToken));
    }
}