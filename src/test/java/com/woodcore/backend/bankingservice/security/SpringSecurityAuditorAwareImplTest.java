package com.woodcore.backend.bankingservice.security;

import com.woodcore.backend.bankingservice.utils.ResponseConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SpringSecurityAuditorAwareImplTest {

    @Mock
    private Authentication authentication;
    @InjectMocks
    private SpringSecurityAuditorAwareImpl springSecurityAuditorAware;

    @BeforeEach
    public void init() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("ensure auditor is logged in user")
    public void getCurrentAuditorAndReturnLoggedInUser() {
        given(authentication.getPrincipal()).willReturn("test-user-id");
        Optional<String> currentAuditor = springSecurityAuditorAware.getCurrentAuditor();
        assertEquals(Optional.of("test-user-id"), currentAuditor);
    }

    @Test
    @DisplayName("ensure auditor is system when system makes db edits")
    public void getCurrentAuditorAndSystem() {
        given(authentication.getPrincipal()).willReturn(null);
        Optional<String> currentAuditor = springSecurityAuditorAware.getCurrentAuditor();
        assertEquals(Optional.of(ResponseConstants.SYSTEM), currentAuditor);
    }
}