package com.example.backend.application.usecases.credentials.toggl;

import com.example.backend.application.dto.credentials.toggl.TogglCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.toggl.TogglCredentialResponseDto;
import com.example.backend.domain.credentials.toggl.TogglCredential;
import com.example.backend.domain.credentials.toggl.ITogglCredentialRepository;
import com.example.backend.infrastructure.toggl.TogglApiService;

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
public class TogglCredentialUseCase {
    
    private final ITogglCredentialRepository togglCredentialRepository;
    private final TogglApiService togglApiService;
    
    public TogglCredentialResponseDto create(UUID userId, TogglCredentialCreateRequestDto request) {
        // 既存のアクティブな認証情報を無効化
        List<TogglCredential> existingCredentials = togglCredentialRepository.findAllByUserId(userId);
        for (TogglCredential existing : existingCredentials) {
            if (existing.isActive()) {
                TogglCredential updated = existing.toBuilder()
                    .isActive(false)
                    .updatedAt(LocalDateTime.now())
                    .build();
                togglCredentialRepository.save(updated);
            }
        }
        
        TogglCredential credential = TogglCredential.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .apiKey(request.getApiKey())
                .workspaceId(request.getWorkspaceId())
                .projectIds(request.getProjectIds())
                .defaultTags(request.getDefaultTags())
                .timeZone(request.getTimeZone() != null ? request.getTimeZone() : "UTC")
                .includeWeekends(request.getIncludeWeekends() != null ? request.getIncludeWeekends() : false)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        TogglCredential saved = togglCredentialRepository.save(credential);
        return TogglCredentialResponseDto.from(saved);
    }
    
    
    @Transactional(readOnly = true)
    public List<TogglCredentialResponseDto> findAllByUserId(UUID userId) {
        return togglCredentialRepository.findAllByUserId(userId).stream()
                .map(TogglCredentialResponseDto::from)
                .toList();
    }
    
    public void deleteById(UUID id) {
        if (!togglCredentialRepository.existsById(id)) {
            throw new RuntimeException("Toggl認証情報が見つかりません: " + id);
        }
        togglCredentialRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countByUserId(UUID userId) {
        return togglCredentialRepository.countByUserId(userId);
    }
    
    /**
     * Toggl Track接続テスト（汎用）
     * 
     * @return 接続成功時true
     */
    @Transactional(readOnly = true)
    public boolean testConnection() {
        return togglCredentialRepository.testConnection();
    }
}
