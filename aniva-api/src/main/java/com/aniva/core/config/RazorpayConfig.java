package com.aniva.core.config;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient(
            @Value("${razorpay.key}") String razorpayKey,
            @Value("${razorpay.secret}") String razorpaySecret) throws Exception {

        return new RazorpayClient(
                razorpayKey,
                razorpaySecret
        );
    }
}
