package com.example.backend.domain.credentials.github;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class GitHubCredential {
    
    private final UUID id;
    private final UUID userId;
    private final String apiKey;
    private final String baseUrl;
    private final String owner;
    private final String repo;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public boolean hasOwner() {
        return owner != null && !owner.trim().isEmpty();
    }
    
    public boolean hasRepo() {
        return repo != null && !repo.trim().isEmpty();
    }
    
    public boolean hasOwnerAndRepo() {
        return hasOwner() && hasRepo();
    }
    
    public String getFullRepoName() {
        if (hasOwnerAndRepo()) {
            return owner + "/" + repo;
        }
        return "";
    }
}
