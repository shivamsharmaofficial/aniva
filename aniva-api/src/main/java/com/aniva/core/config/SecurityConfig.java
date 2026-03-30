package com.aniva.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.aniva.core.security.JwtAuthenticationFilter;
import com.aniva.core.security.LoggingFilter;
import com.aniva.core.security.RateLimitingFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoggingFilter loggingFilter;
    private final RateLimitingFilter rateLimitingFilter;

    /*
     ===============================
     PASSWORD ENCODER
     ===============================
    */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     ===============================
     SECURITY FILTER CHAIN
     ===============================
    */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF (JWT based)
            .csrf(csrf -> csrf.disable())

            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Stateless session
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // API Authorization Rules
            .authorizeHttpRequests(auth -> auth

                    // Public APIs
                    .requestMatchers(
                            "/api/auth/**",
                            "/api/products/**",
                            "/api/categories/**",
                            "/api/payments/mode",
                            "/api/cart/**",
                            "/api/webhook/razorpay"
                    ).permitAll()

                    // Admin APIs
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")

                    // Everything else requires authentication
                    .anyRequest().authenticated()
            )

            // Disable default login mechanisms
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())

        // Add JWT filter
        .addFilterBefore(
                loggingFilter,
                UsernamePasswordAuthenticationFilter.class
        )
        .addFilterBefore(
                rateLimitingFilter,
                UsernamePasswordAuthenticationFilter.class
        )
        .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    /*
     ===============================
     CORS CONFIGURATION
     ===============================
    */

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://aniva-frontend.onrender.com"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
        }
}
