package com.woodcore.backend.bankingservice.service.impl;

import com.woodcore.backend.bankingservice.dto.request.RegisterUserRequest;
import com.woodcore.backend.bankingservice.dto.request.UserLoginRequest;
import com.woodcore.backend.bankingservice.exception.EmailAlreadyExistsException;
import com.woodcore.backend.bankingservice.model.Authority;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.model.enums.UserType;
import com.woodcore.backend.bankingservice.repository.AuthorityRepository;
import com.woodcore.backend.bankingservice.repository.UserRepository;
import com.woodcore.backend.bankingservice.security.jwt.JwtToken;
import com.woodcore.backend.bankingservice.security.jwt.JwtTokenProvider;
import com.woodcore.backend.bankingservice.utils.AuthoritiesConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthorityRepository authorityRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserServiceImpl userService;

    private User user;
    private RegisterUserRequest registerUserRequest;
    private Authority userAuthority;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstName("test-user-first-name")
                .lastName("test-user-last-name")
                .password("test-password-12345")
                .email("test-user@example.com")
                .phoneNumber("08102023276")
                .userType(UserType.USER)
                .build();

        user.setId(UUID.randomUUID());

        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setFirstName("test-first-name");
        registerUserRequest.setLastName("test-last-name");
        registerUserRequest.setEmail("test-user@example.com");
        registerUserRequest.setPhoneNumber("08102023276");
        registerUserRequest.setPassword("password123$");

        userAuthority = new Authority();
        userAuthority.setAuthorityName(AuthoritiesConstants.USER);
    }

    @Test
    public void successfulUserRegistration() {

        BDDMockito.given(userRepository.findByEmailIgnoreCase(registerUserRequest.getEmail())).willReturn(Optional.empty());
        BDDMockito.given(userRepository.findByPhoneNumber(registerUserRequest.getPhoneNumber())).willReturn(Optional.empty());
        BDDMockito.given(passwordEncoder.encode(Mockito.any(String.class))).willReturn("password-hash");
        BDDMockito.given(authorityRepository.findByAuthorityName(AuthoritiesConstants.USER)).willReturn(Optional.of(userAuthority));
        BDDMockito.given(userRepository.save(Mockito.any(User.class))).willReturn(user);

        User registeredUser = userService.registerUser(registerUserRequest);

        Assertions.assertThat(registeredUser.getEmail()).isEqualTo("test-user@example.com");
        Assertions.assertThat(registeredUser.getPhoneNumber()).isEqualTo("08102023276");
    }

    @Test
    public void failedRegistrationIfEmailAlreadyExists() {

        BDDMockito.given(userRepository.findByEmailIgnoreCase(registerUserRequest.getEmail())).willReturn(Optional.of(user));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> userService.registerUser(registerUserRequest));

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }
}