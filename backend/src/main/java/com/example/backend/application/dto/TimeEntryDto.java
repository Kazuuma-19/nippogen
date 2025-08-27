package com.example.backend.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TimeEntryDto {
    private final Long id;
    private final String description;
    private final String projectName;
    private final Long projectId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Long durationSeconds;
    private final boolean billable;
    private final String userId;
    private final String workspaceName;
    private final Long workspaceId;
    private final String[] tags;
}