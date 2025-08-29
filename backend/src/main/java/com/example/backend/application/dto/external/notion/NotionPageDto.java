package com.example.backend.application.dto.external.notion;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NotionPageDto {
    private final String id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String url;
    private final List<String> tags;
    private final String status;
}
