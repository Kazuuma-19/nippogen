package com.example.backend.domain.credentials.github;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IGitHubCredentialRepository {
    
    GitHubCredential save(GitHubCredential credential);
    
    Optional<GitHubCredential> findById(UUID id);
    
    Optional<GitHubCredential> findByUserId(UUID userId);
    
    List<GitHubCredential> findAllByUserId(UUID userId);
    
    List<GitHubCredential> findActiveByUserId(UUID userId);
    
    boolean existsById(UUID id);
    
    boolean existsByUserId(UUID userId);
    
    void deleteById(UUID id);
    
    void deleteByUserId(UUID userId);
    
    long countByUserId(UUID userId);
}