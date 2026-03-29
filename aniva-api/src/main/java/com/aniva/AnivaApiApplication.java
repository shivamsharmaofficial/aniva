package com.aniva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableJpaAuditing
@EnableMethodSecurity
@EnableScheduling
@EnableRetry
@SpringBootApplication
public class AnivaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnivaApiApplication.class, args);
	}

}
