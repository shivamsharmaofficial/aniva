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

    // 🔥 Stores bucket per (IP + endpoint)
    private final Map<String, Bucket> bucketsByIp = new ConcurrentHashMap<>();

    /*
     ===============================
     CREATE BUCKET BASED ON PATH
     ===============================
     - Different rate limits for different endpoints
    */
    private Bucket createBucket(String path) {

        // 🔐 STRICT LIMIT FOR LOGIN (prevent brute force)
        if (path.startsWith("/api/auth/login")) {
            return Bucket.builder()
                    .addLimit(
                            Bandwidth.classic(
                                    5, // max 5 requests
                                    Refill.greedy(5, Duration.ofMinutes(1)) // per minute
                            )
                    )
                    .build();
        }

        // 🔓 NORMAL LIMIT FOR OTHER APIs
        return Bucket.builder()
                .addLimit(
                        Bandwidth.classic(
                                100, // max 100 requests
                                Refill.greedy(100, Duration.ofMinutes(1))
                        )
                )
                .build();
    }

    /*
     ===============================
     FILTER ONLY IMPORTANT APIs
     ===============================
    */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return !(path.startsWith(AUTH_PATH_PREFIX) ||
                 path.startsWith(ORDERS_PATH_PREFIX) ||
                 path.startsWith(PAYMENTS_PATH_PREFIX));
    }

    /*
     ===============================
     MAIN RATE LIMIT LOGIC
     ===============================
    */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = resolveClientIp(request);

        // 🔥 NEW: Get request path
        String path = request.getServletPath();

        // 🔥 CRITICAL FIX:
        // Combine IP + path so each endpoint has separate rate limit
        // Example:
        // 192.168.1.1:/api/auth/login
        // 192.168.1.1:/api/orders/create
        String key = clientIp + ":" + path;

        // 🔥 FIXED: Create bucket per (IP + endpoint)
        Bucket bucket = bucketsByIp.computeIfAbsent(
                key,
                k -> createBucket(path)
        );

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

    /*
     ===============================
     EXTRACT CLIENT IP
     ===============================
     - Supports proxies (NGINX, Cloudflare, etc.)
    */
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