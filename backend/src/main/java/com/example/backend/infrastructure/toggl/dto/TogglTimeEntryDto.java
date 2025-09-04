package com.example.backend.infrastructure.toggl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TogglTimeEntryDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("workspace_id")
    private Long workspaceId;
    
    @JsonProperty("project_id")
    private Long projectId;
    
    @JsonProperty("task_id")
    private Long taskId;
    
    @JsonProperty("billable")
    private Boolean billable;
    
    @JsonProperty("start")
    private LocalDateTime start;
    
    @JsonProperty("stop")
    private LocalDateTime stop;
    
    @JsonProperty("duration")
    private Long duration; // 秒単位
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("tags")
    private List<String> tags;
    
    @JsonProperty("tag_ids")
    private List<Long> tagIds;
    
    @JsonProperty("duronly")
    private Boolean duronly;
    
    @JsonProperty("at")
    private LocalDateTime at; // 最終更新日時
    
    @JsonProperty("server_deleted_at")
    private LocalDateTime serverDeletedAt;
    
    @JsonProperty("user_id")
    private Long userId;
    
    @JsonProperty("uid")
    private Long uid;
    
    @JsonProperty("wid")
    private Long wid; // workspace ID (legacy)
    
    @JsonProperty("pid")
    private Long pid; // project ID (legacy)
    
    // Utility methods
    public double getDurationHours() {
        return duration != null ? duration / 3600.0 : 0.0;
    }
    
    public String getFormattedDuration() {
        if (duration == null) {
            return "00:00:00";
        }
        
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public boolean isRunning() {
        return duration != null && duration < 0;
    }
}