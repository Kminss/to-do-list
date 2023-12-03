package com.sparta.todo.config;

import java.util.Optional;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@TestConfiguration
public class JpaTestConfig {
	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> Optional.of("mins");
	}
}
