package com.example.backend.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubCommitDto(
        String sha,
        @JsonProperty("html_url") String htmlUrl,
        GitHubCommitDetailDto commit,
        GitHubUserDto author,
        GitHubUserDto committer
) {
}