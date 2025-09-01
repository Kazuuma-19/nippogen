package com.example.backend.domain.credentials.github;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IGitHubCredentialRepository {
    /**
     * GitHub接続をテストする
     *
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @return 接続成功時true
     */
    boolean testConnection(String owner, String repo);

    GitHubCredential save(GitHubCredential credential);
    
    
    List<GitHubCredential> findAllByUserId(UUID userId);
    
    List<GitHubCredential> findActiveByUserId(UUID userId);
    
    boolean existsById(UUID id);
    
    boolean existsByUserId(UUID userId);
    
    void deleteById(UUID id);
    
    void deleteByUserId(UUID userId);
    
    long countByUserId(UUID userId);
}