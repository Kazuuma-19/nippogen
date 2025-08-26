package com.example.backend.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubRepositoryDto(
        Long id,
        String name,
        @JsonProperty("full_name") String fullName,
        String description,
        @JsonProperty("private") boolean isPrivate,
        String url,
        @JsonProperty("html_url") String htmlUrl,
        GitHubOwnerDto owner
) {
}