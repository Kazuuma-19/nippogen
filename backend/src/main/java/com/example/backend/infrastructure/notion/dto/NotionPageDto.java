package com.example.backend.infrastructure.notion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotionPageDto {
    
    @JsonProperty("object")
    private String object; // "page"
    
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
    
    @JsonProperty("cover")
    private CoverInfo cover;
    
    @JsonProperty("icon")
    private IconInfo icon;
    
    @JsonProperty("parent")
    private ParentInfo parent;
    
    @JsonProperty("archived")
    private Boolean archived;
    
    @JsonProperty("properties")
    private Map<String, Object> properties;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("public_url")
    private String publicUrl;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoverInfo {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("external")
        private ExternalFile external;
        
        @JsonProperty("file")
        private FileInfo file;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IconInfo {
        @JsonProperty("type")
        private String type; // "emoji", "external", "file"
        
        @JsonProperty("emoji")
        private String emoji;
        
        @JsonProperty("external")
        private ExternalFile external;
        
        @JsonProperty("file")
        private FileInfo file;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParentInfo {
        @JsonProperty("type")
        private String type; // "database_id", "page_id", "workspace"
        
        @JsonProperty("database_id")
        private String databaseId;
        
        @JsonProperty("page_id")
        private String pageId;
        
        @JsonProperty("workspace")
        private Boolean workspace;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExternalFile {
        @JsonProperty("url")
        private String url;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FileInfo {
        @JsonProperty("url")
        private String url;
        
        @JsonProperty("expiry_time")
        private LocalDateTime expiryTime;
    }
    
    // Utility methods
    public String getTitle() {
        if (properties != null && properties.containsKey("Name")) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> titleProp = (Map<String, Object>) properties.get("Name");
                if (titleProp != null && titleProp.containsKey("title")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> titleArray = (List<Map<String, Object>>) titleProp.get("title");
                    if (titleArray != null && !titleArray.isEmpty()) {
                        return (String) titleArray.get(0).get("plain_text");
                    }
                }
            } catch (Exception e) {
                // プロパティ構造の解析に失敗した場合はnullを返す
            }
        }
        return null;
    }
}