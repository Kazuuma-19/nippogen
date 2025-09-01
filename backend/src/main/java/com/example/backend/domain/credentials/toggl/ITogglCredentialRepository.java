package com.example.backend.domain.credentials.toggl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITogglCredentialRepository {
    
    TogglCredential save(TogglCredential credential);
    
    Optional<TogglCredential> findById(UUID id);
    
    Optional<TogglCredential> findByUserId(UUID userId);
    
    List<TogglCredential> findAllByUserId(UUID userId);
    
    List<TogglCredential> findActiveByUserId(UUID userId);
    
    boolean existsById(UUID id);
    
    boolean existsByUserId(UUID userId);
    
    void deleteById(UUID id);
    
    void deleteByUserId(UUID userId);
    
    long countByUserId(UUID userId);
    
    boolean testConnection();
}