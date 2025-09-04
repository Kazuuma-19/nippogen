package com.example.backend.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepositoryDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("full_name")
    private String fullName;
    
    @JsonProperty("private")
    private Boolean isPrivate;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("html_url")
    private String htmlUrl;
    
    @JsonProperty("clone_url")
    private String cloneUrl;
    
    @JsonProperty("ssh_url")
    private String sshUrl;
    
    @JsonProperty("default_branch")
    private String defaultBranch;
    
    @JsonProperty("language")
    private String language;
    
    @JsonProperty("size")
    private Long size;
    
    @JsonProperty("stargazers_count")
    private Integer stargazersCount;
    
    @JsonProperty("watchers_count")
    private Integer watchersCount;
    
    @JsonProperty("forks_count")
    private Integer forksCount;
    
    @JsonProperty("open_issues_count")
    private Integer openIssuesCount;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("pushed_at")
    private LocalDateTime pushedAt;
    
    @JsonProperty("owner")
    private Owner owner;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {
        
        @JsonProperty("login")
        private String login;
        
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("avatar_url")
        private String avatarUrl;
        
        @JsonProperty("html_url")
        private String htmlUrl;
        
        @JsonProperty("type")
        private String type;
    }
}