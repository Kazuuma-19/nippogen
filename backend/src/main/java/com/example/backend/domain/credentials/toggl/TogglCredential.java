package com.example.backend.domain.credentials.toggl;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class TogglCredential {
    
    private final UUID id;
    private final UUID userId;
    private final String apiKey;
    private final Long workspaceId;
    private final List<Integer> projectIds;
    private final List<String> defaultTags;
    private final String timeZone;
    private final boolean includeWeekends;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public boolean hasProjects() {
        return projectIds != null && !projectIds.isEmpty();
    }
    
    public boolean hasDefaultTags() {
        return defaultTags != null && !defaultTags.isEmpty();
    }

    public int getProjectCount() {
        return hasProjects() ? projectIds.size() : 0;
    }
    
    public int getTagCount() {
        return hasDefaultTags() ? defaultTags.size() : 0;
    }
}
