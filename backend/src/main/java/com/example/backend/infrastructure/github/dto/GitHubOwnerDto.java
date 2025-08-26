package com.example.backend.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubOwnerDto(
        Long id,
        String login,
        @JsonProperty("avatar_url") String avatarUrl,
        String type
) {
}