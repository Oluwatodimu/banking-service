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
import com.woodcore.backend.bankingservice.service.UserService;
import com.woodcore.backend.bankingservice.utils.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User registerUser(RegisterUserRequest registerUserRequest) {

        try {

            ensurePhoneNumberAndEmailDoNotExist(registerUserRequest);
            User newUser = createUser(registerUserRequest);
            Set<Authority> authorities = new HashSet<>();
            authorityRepository.findByAuthorityName(AuthoritiesConstants.USER).ifPresent(authorities::add);
            newUser.setAuthorities(authorities);
            return userRepository.save(newUser);

        } catch (RuntimeException exception) {
            System.err.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage() != null ? exception.getMessage(): "error creating new user");
        }
    }

    private void ensurePhoneNumberAndEmailDoNotExist(RegisterUserRequest request) {
        Optional<User> emailUseroptional = userRepository.findByEmailIgnoreCase(request.getEmail());
        Optional<User> phoneUserOptional = userRepository.findByPhoneNumber(request.getPhoneNumber());

        if (emailUseroptional.isPresent()) {
            throw new EmailAlreadyExistsException("email already exists");
        }

        if (phoneUserOptional.isPresent()) {
            throw new EmailAlreadyExistsException("phone number already exists");
        }
    }

    private User createUser(RegisterUserRequest registerUserRequest) {
        return User.builder()
                .firstName(registerUserRequest.getFirstName().toLowerCase(Locale.ENGLISH))
                .lastName(registerUserRequest.getLastName().toLowerCase(Locale.ENGLISH))
                .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                .email(registerUserRequest.getEmail().toLowerCase(Locale.ENGLISH))
                .phoneNumber(registerUserRequest.getPhoneNumber())
                .userType(UserType.USER)
                .activated(true)
                .build();
    }


    @Override
    public JwtToken authenticateUser(UserLoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
        return jwtTokenProvider.createToken(authentication, loginRequest.isRememberMe());
    }
}
