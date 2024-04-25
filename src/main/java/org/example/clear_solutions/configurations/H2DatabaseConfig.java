package org.example.clear_solutions.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "org.example.clear_solutions.repositories")
@EnableTransactionManagement
public class H2DatabaseConfig {
}