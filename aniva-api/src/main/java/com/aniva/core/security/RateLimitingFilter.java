package com.aniva.core.security;

import com.aniva.core.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final String AUTH_PATH_PREFIX = "/api/auth/";
    private static final String ORDERS_PATH_PREFIX = "/api/orders/";
    private static final String PAYMENTS_PATH_PREFIX = "/api/payments/";

    private final ObjectMapper objectMapper;

    private final Map<String, Bucket> bucketsByIp = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(
                        Bandwidth.classic(
                                100,
                                Refill.greedy(100, Duration.ofMinutes(1))
                        )
                )
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return !(path.startsWith(AUTH_PATH_PREFIX) ||
                 path.startsWith(ORDERS_PATH_PREFIX) ||
                 path.startsWith(PAYMENTS_PATH_PREFIX));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = resolveClientIp(request);

        Bucket bucket = bucketsByIp.computeIfAbsent(clientIp, key -> createBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
            return;
        }

        long waitSeconds = Math.max(
                1,
                Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds()
        );

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", String.valueOf(waitSeconds));

        ApiResponse<Object> error = ApiResponse.failure(
                "Too many requests. Try again after " + waitSeconds + " seconds"
        );

        objectMapper.writeValue(response.getWriter(), error);
    }

    private String resolveClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}
