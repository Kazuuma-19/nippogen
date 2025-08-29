package com.example.backend.application.dto.external.notion;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class NotionTableRowDto {
    private final String id;
    private final String title;
    private final Map<String, Object> properties;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String url;
}
