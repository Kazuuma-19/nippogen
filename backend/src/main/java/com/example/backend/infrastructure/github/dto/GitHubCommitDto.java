package com.example.backend.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubCommitDto {
    
    @JsonProperty("sha")
    private String sha;
    
    @JsonProperty("commit")
    private CommitDetail commit;
    
    @JsonProperty("stats")
    private CommitStats stats;
    
    @JsonProperty("author")
    private GitHubUser author;
    
    @JsonProperty("committer")
    private GitHubUser committer;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitDetail {
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("author")
        private CommitAuthor author;
        
        @JsonProperty("committer") 
        private CommitAuthor committer;
        
        @JsonProperty("tree")
        private TreeInfo tree;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitAuthor {
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("date")
        private LocalDateTime date;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitStats {
        
        @JsonProperty("additions")
        private int additions;
        
        @JsonProperty("deletions")
        private int deletions;
        
        @JsonProperty("total")
        private int total;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubUser {
        
        @JsonProperty("login")
        private String login;
        
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("avatar_url")
        private String avatarUrl;
        
        @JsonProperty("html_url")
        private String htmlUrl;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TreeInfo {
        
        @JsonProperty("sha")
        private String sha;
        
        @JsonProperty("url")
        private String url;
    }
    
    // Utility methods
    public String getCommitMessage() {
        return commit != null ? commit.getMessage() : "";
    }
    
    public String getAuthorName() {
        return commit != null && commit.getAuthor() != null ? 
            commit.getAuthor().getName() : "";
    }
    
    public LocalDateTime getCommitDate() {
        return commit != null && commit.getAuthor() != null ? 
            commit.getAuthor().getDate() : null;
    }
    
    public String getShortSha() {
        return sha != null && sha.length() >= 7 ? sha.substring(0, 7) : sha;
    }
}