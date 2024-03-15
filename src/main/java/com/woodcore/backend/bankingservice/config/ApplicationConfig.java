package com.woodcore.backend.bankingservice.config;

import com.woodcore.backend.bankingservice.config.properties.RateLimiterProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final RateLimiterProperties rateLimiterProperties;

    @Bean
    public RateLimiter rateLimiter() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(rateLimiterProperties.getMaxNumberOfRequests())
                .limitRefreshPeriod(Duration.ofMinutes(rateLimiterProperties.getTimePeriodForRateLimit()))
                .build();

        return RateLimiter.of("api-security-rate-limiter", rateLimiterConfig);
    }
}
