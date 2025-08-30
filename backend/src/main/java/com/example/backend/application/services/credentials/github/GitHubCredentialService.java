package com.example.backend.application.services.credentials.github;

import com.example.backend.application.dto.credentials.github.GitHubCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.github.GitHubCredentialResponseDto;
import com.example.backend.application.dto.credentials.github.GitHubCredentialUpdateRequestDto;
import com.example.backend.domain.credentials.github.GitHubCredential;
import com.example.backend.domain.credentials.github.IGitHubCredentialRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GitHubCredentialService {
    
    private final IGitHubCredentialRepository gitHubCredentialRepository;
    
    public GitHubCredentialResponseDto create(UUID userId, GitHubCredentialCreateRequestDto request) {
        // 既存のアクティブな認証情報を無効化
        gitHubCredentialRepository.findByUserId(userId)
            .ifPresent(existing -> {
                GitHubCredential updated = existing.toBuilder()
                    .isActive(false)
                    .updatedAt(LocalDateTime.now())
                    .build();
                gitHubCredentialRepository.save(updated);
            });
        
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
    public Optional<GitHubCredentialResponseDto> findById(UUID id) {
        return gitHubCredentialRepository.findById(id)
                .map(GitHubCredentialResponseDto::from);
    }
    
    @Transactional(readOnly = true)
    public Optional<GitHubCredentialResponseDto> findByUserId(UUID userId) {
        return gitHubCredentialRepository.findByUserId(userId)
                .map(GitHubCredentialResponseDto::from);
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
    
    public GitHubCredentialResponseDto update(UUID id, GitHubCredentialUpdateRequestDto request) {
        GitHubCredential existing = gitHubCredentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GitHub認証情報が見つかりません: " + id));
        
        GitHubCredential.GitHubCredentialBuilder builder = existing.toBuilder()
                .updatedAt(LocalDateTime.now());
        
        if (request.getApiKey() != null) {
            builder.apiKey(request.getApiKey());
        }
        if (request.getBaseUrl() != null) {
            builder.baseUrl(request.getBaseUrl());
        }
        if (request.getOwner() != null) {
            builder.owner(request.getOwner());
        }
        if (request.getRepo() != null) {
            builder.repo(request.getRepo());
        }
        if (request.getIsActive() != null) {
            builder.isActive(request.getIsActive());
        }
        
        GitHubCredential updated = gitHubCredentialRepository.save(builder.build());
        return GitHubCredentialResponseDto.from(updated);
    }
    
    public void deleteById(UUID id) {
        if (!gitHubCredentialRepository.existsById(id)) {
            throw new RuntimeException("GitHub認証情報が見つかりません: " + id);
        }
        gitHubCredentialRepository.deleteById(id);
    }
    
    public void deleteByUserId(UUID userId) {
        gitHubCredentialRepository.deleteByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        return gitHubCredentialRepository.existsByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public long countByUserId(UUID userId) {
        return gitHubCredentialRepository.countByUserId(userId);
    }
}