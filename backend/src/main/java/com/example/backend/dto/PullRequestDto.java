package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class PullRequestDto {
    private final Long id;
    private final Integer number;
    private final String title;
    private final String body;
    private final String state;
    private final String author;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime mergedAt;
    private final String baseBranch;
    private final String headBranch;
    private final List<String> reviewers;
    private final Integer additions;
    private final Integer deletions;
    private final Integer changedFiles;
    private final String htmlUrl;
}