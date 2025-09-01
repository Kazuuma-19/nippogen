package com.example.backend.application.usecases.credentials.notion;

import com.example.backend.application.dto.credentials.notion.NotionCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.notion.NotionCredentialResponseDto;
import com.example.backend.domain.credentials.notion.NotionCredential;
import com.example.backend.domain.credentials.notion.INotionCredentialRepository;

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
public class NotionCredentialUseCase {
    
    private final INotionCredentialRepository notionCredentialRepository;
    
    public NotionCredentialResponseDto create(UUID userId, NotionCredentialCreateRequestDto request) {
        // 既存のアクティブな認証情報を無効化
        List<NotionCredential> existingCredentials = notionCredentialRepository.findAllByUserId(userId);
        for (NotionCredential existing : existingCredentials) {
            if (existing.isActive()) {
                NotionCredential updated = existing.toBuilder()
                    .isActive(false)
                    .updatedAt(LocalDateTime.now())
                    .build();
                notionCredentialRepository.save(updated);
            }
        }
        
        NotionCredential credential = NotionCredential.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .apiKey(request.getApiKey())
                .databaseId(request.getDatabaseId())
                .titleProperty(request.getTitleProperty() != null ? request.getTitleProperty() : "Name")
                .statusProperty(request.getStatusProperty() != null ? request.getStatusProperty() : "Status")
                .dateProperty(request.getDateProperty() != null ? request.getDateProperty() : "Date")
                .filterConditions(request.getFilterConditions())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        NotionCredential saved = notionCredentialRepository.save(credential);
        return NotionCredentialResponseDto.from(saved);
    }
    
    
    @Transactional(readOnly = true)
    public List<NotionCredentialResponseDto> findAllByUserId(UUID userId) {
        return notionCredentialRepository.findAllByUserId(userId).stream()
                .map(NotionCredentialResponseDto::from)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<NotionCredentialResponseDto> findActiveByUserId(UUID userId) {
        return notionCredentialRepository.findActiveByUserId(userId).stream()
                .map(NotionCredentialResponseDto::from)
                .toList();
    }
    
    
    public void deleteById(UUID id) {
        if (!notionCredentialRepository.existsById(id)) {
            throw new RuntimeException("Notion認証情報が見つかりません: " + id);
        }
        notionCredentialRepository.deleteById(id);
    }
    
    public void deleteByUserId(UUID userId) {
        notionCredentialRepository.deleteByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        return notionCredentialRepository.existsByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public long countByUserId(UUID userId) {
        return notionCredentialRepository.countByUserId(userId);
    }
    
    /**
     * Notion接続テスト
     * 
     * @return 接続成功時true
     */
    @Transactional(readOnly = true)
    public boolean testConnection() {
        return notionCredentialRepository.testConnection();
    }
}