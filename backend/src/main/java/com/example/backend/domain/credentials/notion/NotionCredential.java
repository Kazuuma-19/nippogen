package com.example.backend.domain.credentials.notion;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class NotionCredential {
    
    private final UUID id;
    private final UUID userId;
    private final String apiKey;
    private final String databaseId;
    private final String titleProperty;
    private final String statusProperty;
    private final String dateProperty;
    private final Map<String, Object> filterConditions;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public boolean hasDatabase() {
        return databaseId != null && !databaseId.trim().isEmpty();
    }
    
    public boolean hasTitleProperty() {
        return titleProperty != null && !titleProperty.trim().isEmpty();
    }
    
    public boolean hasStatusProperty() {
        return statusProperty != null && !statusProperty.trim().isEmpty();
    }
    
    public boolean hasDateProperty() {
        return dateProperty != null && !dateProperty.trim().isEmpty();
    }
    
    
    
    
    
    public boolean isValid() {
        return apiKey != null && !apiKey.trim().isEmpty() && userId != null;
    }
    
    public boolean isFullyConfigured() {
        return isValid() && hasDatabase() && hasTitleProperty();
    }
}