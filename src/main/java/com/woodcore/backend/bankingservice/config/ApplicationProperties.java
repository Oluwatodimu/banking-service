package com.woodcore.backend.bankingservice.config;

import com.woodcore.backend.bankingservice.config.properties.RateLimiterProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {

    @Bean
    @ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
    public RateLimiterProperties rateLimiterProperties() {
        return new RateLimiterProperties();
    }
}
