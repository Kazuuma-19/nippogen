package com.example.backend.application.usecases.credentials.toggle;

import com.example.backend.application.dto.credentials.toggle.ToggleCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.toggle.ToggleCredentialResponseDto;
import com.example.backend.application.dto.credentials.toggle.ToggleCredentialUpdateRequestDto;
import com.example.backend.domain.credentials.toggle.ToggleCredential;
import com.example.backend.domain.credentials.toggle.IToggleCredentialRepository;

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
public class ToggleCredentialUseCase {
    
    private final IToggleCredentialRepository toggleCredentialRepository;
    
    public ToggleCredentialResponseDto create(UUID userId, ToggleCredentialCreateRequestDto request) {
        // 既存のアクティブな認証情報を無効化
        toggleCredentialRepository.findByUserId(userId)
            .ifPresent(existing -> {
                ToggleCredential updated = existing.toBuilder()
                    .isActive(false)
                    .updatedAt(LocalDateTime.now())
                    .build();
                toggleCredentialRepository.save(updated);
            });
        
        ToggleCredential credential = ToggleCredential.builder()
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
        
        ToggleCredential saved = toggleCredentialRepository.save(credential);
        return ToggleCredentialResponseDto.from(saved);
    }
    
    @Transactional(readOnly = true)
    public Optional<ToggleCredentialResponseDto> findById(UUID id) {
        return toggleCredentialRepository.findById(id)
                .map(ToggleCredentialResponseDto::from);
    }
    
    @Transactional(readOnly = true)
    public Optional<ToggleCredentialResponseDto> findByUserId(UUID userId) {
        return toggleCredentialRepository.findByUserId(userId)
                .map(ToggleCredentialResponseDto::from);
    }
    
    @Transactional(readOnly = true)
    public List<ToggleCredentialResponseDto> findAllByUserId(UUID userId) {
        return toggleCredentialRepository.findAllByUserId(userId).stream()
                .map(ToggleCredentialResponseDto::from)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ToggleCredentialResponseDto> findActiveByUserId(UUID userId) {
        return toggleCredentialRepository.findActiveByUserId(userId).stream()
                .map(ToggleCredentialResponseDto::from)
                .toList();
    }
    
    public ToggleCredentialResponseDto update(UUID id, ToggleCredentialUpdateRequestDto request) {
        ToggleCredential existing = toggleCredentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Toggle認証情報が見つかりません: " + id));
        
        ToggleCredential.ToggleCredentialBuilder builder = existing.toBuilder()
                .updatedAt(LocalDateTime.now());
        
        if (request.getApiKey() != null) {
            builder.apiKey(request.getApiKey());
        }
        if (request.getWorkspaceId() != null) {
            builder.workspaceId(request.getWorkspaceId());
        }
        if (request.getProjectIds() != null) {
            builder.projectIds(request.getProjectIds());
        }
        if (request.getDefaultTags() != null) {
            builder.defaultTags(request.getDefaultTags());
        }
        if (request.getTimeZone() != null) {
            builder.timeZone(request.getTimeZone());
        }
        if (request.getIncludeWeekends() != null) {
            builder.includeWeekends(request.getIncludeWeekends());
        }
        if (request.getIsActive() != null) {
            builder.isActive(request.getIsActive());
        }
        
        ToggleCredential updated = toggleCredentialRepository.save(builder.build());
        return ToggleCredentialResponseDto.from(updated);
    }
    
    public void deleteById(UUID id) {
        if (!toggleCredentialRepository.existsById(id)) {
            throw new RuntimeException("Toggle認証情報が見つかりません: " + id);
        }
        toggleCredentialRepository.deleteById(id);
    }
    
    public void deleteByUserId(UUID userId) {
        toggleCredentialRepository.deleteByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        return toggleCredentialRepository.existsByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public long countByUserId(UUID userId) {
        return toggleCredentialRepository.countByUserId(userId);
    }
}