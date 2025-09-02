package com.example.backend.application.usecases.credentials.github;

import com.example.backend.application.dto.credentials.github.GitHubCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.github.GitHubCredentialResponseDto;
import com.example.backend.domain.credentials.github.GitHubCredential;
import com.example.backend.domain.credentials.github.IGitHubCredentialRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GitHubCredentialUseCase {
    
    private final IGitHubCredentialRepository gitHubCredentialRepository;

    public GitHubCredentialResponseDto create(UUID userId, GitHubCredentialCreateRequestDto request) {
        // 既存のアクティブな認証情報を無効化
        List<GitHubCredential> existingCredentials = gitHubCredentialRepository.findAllByUserId(userId);
        for (GitHubCredential existing : existingCredentials) {
            if (existing.isActive()) {
                GitHubCredential updated = existing.toBuilder()
                    .isActive(false)
                    .updatedAt(LocalDateTime.now())
                    .build();
                gitHubCredentialRepository.save(updated);
            }
        }
        
        GitHubCredential credential = GitHubCredential.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .apiKey(request.getApiKey())
                .baseUrl(request.getBaseUrl() != null ? request.getBaseUrl() : "https://api.github.com")
                .owner(request.getOwner())
                .repo(request.getRepo())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        GitHubCredential saved = gitHubCredentialRepository.save(credential);
        return GitHubCredentialResponseDto.from(saved);
    }
    
    
    @Transactional(readOnly = true)
    public List<GitHubCredentialResponseDto> findAllByUserId(UUID userId) {
        return gitHubCredentialRepository.findAllByUserId(userId).stream()
                .map(GitHubCredentialResponseDto::from)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<GitHubCredentialResponseDto> findActiveByUserId(UUID userId) {
        return gitHubCredentialRepository.findActiveByUserId(userId).stream()
                .map(GitHubCredentialResponseDto::from)
                .toList();
    }
    
    
    public void deleteById(UUID id) {
        if (!gitHubCredentialRepository.existsById(id)) {
            throw new RuntimeException("GitHub認証情報が見つかりません: " + id);
        }
        gitHubCredentialRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countByUserId(UUID userId) {
        return gitHubCredentialRepository.countByUserId(userId);
    }
    
    /**
     * GitHub接続テスト
     * 
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @return 接続成功時true
     */
    @Transactional(readOnly = true)
    public boolean testConnection(String owner, String repo) {
        return gitHubCredentialRepository.testConnection(owner, repo);
    }
}
