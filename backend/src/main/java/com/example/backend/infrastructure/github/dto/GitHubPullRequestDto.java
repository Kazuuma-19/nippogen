package com.example.backend.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record GitHubPullRequestDto(
        Long id,
        int number,
        String title,
        String body,
        String state,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        @JsonProperty("merged_at") LocalDateTime mergedAt,
        @JsonProperty("html_url") String htmlUrl,
        GitHubUserDto user,
        GitHubUserDto assignee
) {
}