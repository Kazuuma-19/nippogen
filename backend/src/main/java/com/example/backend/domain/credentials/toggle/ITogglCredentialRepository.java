package com.example.backend.domain.credentials.toggle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IToggleCredentialRepository {
    
    ToggleCredential save(ToggleCredential credential);
    
    Optional<ToggleCredential> findById(UUID id);
    
    Optional<ToggleCredential> findByUserId(UUID userId);
    
    List<ToggleCredential> findAllByUserId(UUID userId);
    
    List<ToggleCredential> findActiveByUserId(UUID userId);
    
    boolean existsById(UUID id);
    
    boolean existsByUserId(UUID userId);
    
    void deleteById(UUID id);
    
    void deleteByUserId(UUID userId);
    
    long countByUserId(UUID userId);
}