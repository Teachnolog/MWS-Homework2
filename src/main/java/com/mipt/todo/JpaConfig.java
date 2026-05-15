package com.mipt.todo;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@ConditionalOnClass(EntityManagerFactory.class)
@EnableJpaAuditing
public class JpaConfig {
}
