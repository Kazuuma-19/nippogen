package com.example.backend.infrastructure.notion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotionDatabaseDto {
    
    @JsonProperty("object")
    private String object; // "database"
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("created_time")
    private LocalDateTime createdTime;
    
    @JsonProperty("last_edited_time")
    private LocalDateTime lastEditedTime;
    
    @JsonProperty("created_by")
    private NotionUserDto createdBy;
    
    @JsonProperty("last_edited_by")
    private NotionUserDto lastEditedBy;
    
    @JsonProperty("title")
    private Map<String, Object> title;
    
    @JsonProperty("description")
    private Map<String, Object> description;
    
    @JsonProperty("icon")
    private NotionPageDto.IconInfo icon;
    
    @JsonProperty("cover")
    private NotionPageDto.CoverInfo cover;
    
    @JsonProperty("properties")
    private Map<String, Object> properties;
    
    @JsonProperty("parent")
    private NotionPageDto.ParentInfo parent;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("archived")
    private Boolean archived;
    
    @JsonProperty("is_inline")
    private Boolean isInline;
    
    @JsonProperty("public_url")
    private String publicUrl;
}