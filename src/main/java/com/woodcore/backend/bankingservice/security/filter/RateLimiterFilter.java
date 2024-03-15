package com.woodcore.backend.bankingservice.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woodcore.backend.bankingservice.dto.BaseResponse;
import com.woodcore.backend.bankingservice.exception.RateLimiterException;
import com.woodcore.backend.bankingservice.utils.AuthoritiesConstants;
import io.github.resilience4j.ratelimiter.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends GenericFilterBean {

    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader("x-rateLimit-limit", String.valueOf(rateLimiter.getRateLimiterConfig().getLimitForPeriod()));
        httpServletResponse.setHeader("x-rateLimit-remaining", String.valueOf(rateLimiter.getMetrics().getAvailablePermissions()));
        httpServletResponse.setHeader("x-rateLimit-reset", String.valueOf(rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod().getSeconds()));

        try {

            if (!rateLimiter.acquirePermission()) {
                throw new RateLimiterException("too many requests");
            }
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (RateLimiterException exception) {
            System.err.println(exception.getMessage());
            BaseResponse baseResponse = new BaseResponse(null, exception.getMessage(), true);
            httpServletResponse.setContentType(AuthoritiesConstants.ACCEPT);
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(baseResponse));
        }
    }
}
