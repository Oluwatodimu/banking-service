package com.woodcore.backend.bankingservice.security;

import com.woodcore.backend.bankingservice.exception.UserNotActivatedException;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username.toLowerCase(Locale.ENGLISH)) // username is email in this case
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("email/password not correct"));
    }

    @SneakyThrows
    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User not activated");
        }

        List<GrantedAuthority> grantedAuthorities = user
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassword(), grantedAuthorities);
    }
}
