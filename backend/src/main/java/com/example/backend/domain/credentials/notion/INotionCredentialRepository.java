package com.example.backend.domain.credentials.notion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface INotionCredentialRepository {
    
    NotionCredential save(NotionCredential credential);
    
    
    List<NotionCredential> findAllByUserId(UUID userId);
    
    List<NotionCredential> findActiveByUserId(UUID userId);
    
    boolean existsById(UUID id);
    
    boolean existsByUserId(UUID userId);
    
    void deleteById(UUID id);
    
    void deleteByUserId(UUID userId);
    
    long countByUserId(UUID userId);
    
    boolean testConnection();
}