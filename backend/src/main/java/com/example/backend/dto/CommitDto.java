package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
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