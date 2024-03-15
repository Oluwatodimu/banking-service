package com.woodcore.backend.bankingservice.config;

import com.woodcore.backend.bankingservice.security.SpringSecurityAuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.woodcore.backend.bankingservice.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAwareImpl")
public class DatabaseAuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SpringSecurityAuditorAwareImpl();
    }
}
