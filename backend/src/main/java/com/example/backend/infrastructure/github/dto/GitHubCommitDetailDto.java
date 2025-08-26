package com.example.backend.infrastructure.github.dto;

import java.time.LocalDateTime;

public record GitHubCommitDetailDto(
        String message,
        GitHubCommitAuthorDto author,
        GitHubCommitAuthorDto committer
) {
    public record GitHubCommitAuthorDto(
            String name,
            String email,
            LocalDateTime date
    ) {
    }
}