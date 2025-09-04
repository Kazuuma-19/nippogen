package com.example.backend.infrastructure.notion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotionUserDto {
    
    @JsonProperty("object")
    private String object; // "user"
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("avatar_url")
    private String avatarUrl;
    
    @JsonProperty("type")
    private String type; // "person" or "bot"
    
    @JsonProperty("person")
    private PersonInfo person;
    
    @JsonProperty("bot")
    private BotInfo bot;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonInfo {
        @JsonProperty("email")
        private String email;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BotInfo {
        @JsonProperty("owner")
        private Object owner;
        
        @JsonProperty("workspace_name")
        private String workspaceName;
    }
}