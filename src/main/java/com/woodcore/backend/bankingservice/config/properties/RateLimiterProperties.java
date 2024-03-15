package com.woodcore.backend.bankingservice.config.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimiterProperties {
    private int maxNumberOfRequests;
    private int timePeriodForRateLimit;
}
