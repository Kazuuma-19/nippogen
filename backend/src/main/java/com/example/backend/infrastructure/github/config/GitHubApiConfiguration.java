package com.example.backend.infrastructure.github.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class GitHubApiConfiguration {

    @Value("${spring.application.github.api.base-url}")
    private String baseUrl;

    @Value("${spring.application.github.api.token}")
    private String token;

    @Value("${spring.application.github.api.timeout:30s}")
    private Duration timeout;

    @Bean
    public WebClient gitHubWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }
}