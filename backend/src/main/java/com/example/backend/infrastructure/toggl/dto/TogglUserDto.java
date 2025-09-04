package com.example.backend.infrastructure.toggl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TogglUserDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("api_token")
    private String apiToken;
    
    @JsonProperty("default_workspace_id")
    private Long defaultWorkspaceId;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("fullname")
    private String fullname;
    
    @JsonProperty("jquery_timeofday_format")
    private String jqueryTimeofdayFormat;
    
    @JsonProperty("jquery_date_format")
    private String jqueryDateFormat;
    
    @JsonProperty("timeofday_format")
    private String timeofdayFormat;
    
    @JsonProperty("date_format")
    private String dateFormat;
    
    @JsonProperty("store_start_and_stop_time")
    private Boolean storeStartAndStopTime;
    
    @JsonProperty("beginning_of_week")
    private Integer beginningOfWeek;
    
    @JsonProperty("language")
    private String language;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("sidebar_piechart")
    private Boolean sidebarPiechart;
    
    @JsonProperty("at")
    private LocalDateTime at;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("timezone")
    private String timezone;
    
    @JsonProperty("has_password")
    private Boolean hasPassword;
    
    @JsonProperty("openid_enabled")
    private Boolean openidEnabled;
    
    @JsonProperty("openid_email")
    private String openidEmail;
}