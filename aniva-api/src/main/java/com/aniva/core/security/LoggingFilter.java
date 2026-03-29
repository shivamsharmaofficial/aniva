package com.aniva.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final long SLOW_API_THRESHOLD_MS = 1000;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String ip = resolveClientIp(request);
        String path = query == null ? uri : uri + "?" + query;

        try {
            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - start;

            if (duration > SLOW_API_THRESHOLD_MS) {
                log.warn(
                        "slow_api method={} path=\"{}\" status={} durationMs={} clientIp={}",
                        method,
                        path,
                        response.getStatus(),
                        duration,
                        ip
                );
            }

            log.info(
                    "api_request method={} path=\"{}\" status={} durationMs={} clientIp={}",
                    method,
                    path,
                    response.getStatus(),
                    duration,
                    ip
            );

        } catch (Exception ex) {

            long duration = System.currentTimeMillis() - start;

            log.error(
                    "api_error method={} path=\"{}\" status={} durationMs={} clientIp={} errorType={} message=\"{}\"",
                    method,
                    path,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    duration,
                    ip,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
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
