package com.example.backend.application.dto.external.github;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommitDto {
    private final String sha;
    private final String message;
    private final String author;
    private final String authorEmail;
    private final LocalDateTime date;
    private final Integer additions;
    private final Integer deletions;
    private final Integer total;
    private final String htmlUrl;
}
