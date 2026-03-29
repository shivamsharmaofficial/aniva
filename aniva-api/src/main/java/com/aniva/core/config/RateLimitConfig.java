package com.aniva.core.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bandwidth apiRateLimitBandwidth() {
        return Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1)));
    }
}
