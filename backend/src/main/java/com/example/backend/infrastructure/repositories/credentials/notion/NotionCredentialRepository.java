package com.example.backend.infrastructure.repositories.credentials.notion;

import static com.example.backend.jooq.tables.JNotionCredentials.NOTION_CREDENTIALS;

import com.example.backend.domain.credentials.notion.NotionCredential;
import com.example.backend.domain.credentials.notion.INotionCredentialRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotionCredentialRepository implements INotionCredentialRepository {
    
    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    @Override
    public NotionCredential save(NotionCredential credential) {
        if (existsById(credential.getId())) {
            return update(credential);
        } else {
            return insert(credential);
        }
    }

    @Override
    public Optional<NotionCredential> findById(UUID id) {
        return dsl.selectFrom(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.ID.eq(id))
                .fetchOptional()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<NotionCredential> findByUserId(UUID userId) {
        return dsl.selectFrom(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.USER_ID.eq(userId)
                       .and(NOTION_CREDENTIALS.IS_ACTIVE.eq(true)))
                .fetchOptional()
                .map(this::mapToEntity);
    }

    @Override
    public List<NotionCredential> findAllByUserId(UUID userId) {
        return dsl.selectFrom(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.USER_ID.eq(userId))
                .orderBy(NOTION_CREDENTIALS.CREATED_AT.desc())
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<NotionCredential> findActiveByUserId(UUID userId) {
        return dsl.selectFrom(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.USER_ID.eq(userId)
                       .and(NOTION_CREDENTIALS.IS_ACTIVE.eq(true)))
                .orderBy(NOTION_CREDENTIALS.CREATED_AT.desc())
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public boolean existsById(UUID id) {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.ID.eq(id))
        );
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.USER_ID.eq(userId)
                       .and(NOTION_CREDENTIALS.IS_ACTIVE.eq(true)))
        );
    }

    @Override
    public void deleteById(UUID id) {
        dsl.deleteFrom(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.ID.eq(id))
                .execute();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        dsl.deleteFrom(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.USER_ID.eq(userId))
                .execute();
    }

    @Override
    public long countByUserId(UUID userId) {
        return dsl.selectCount()
                .from(NOTION_CREDENTIALS)
                .where(NOTION_CREDENTIALS.USER_ID.eq(userId)
                       .and(NOTION_CREDENTIALS.IS_ACTIVE.eq(true)))
                .fetchOne(0, Long.class);
    }
    
    @Override
    public boolean testConnection() {
        // TODO: 実際のNotion API接続テストを実装
        return true;
    }

    private NotionCredential insert(NotionCredential credential) {
        var record = dsl.insertInto(NOTION_CREDENTIALS)
                .set(NOTION_CREDENTIALS.ID, credential.getId())
                .set(NOTION_CREDENTIALS.USER_ID, credential.getUserId())
                .set(NOTION_CREDENTIALS.API_KEY, credential.getApiKey())
                .set(NOTION_CREDENTIALS.DATABASE_ID, credential.getDatabaseId())
                .set(NOTION_CREDENTIALS.TITLE_PROPERTY, credential.getTitleProperty())
                .set(NOTION_CREDENTIALS.STATUS_PROPERTY, credential.getStatusProperty())
                .set(NOTION_CREDENTIALS.DATE_PROPERTY, credential.getDateProperty())
                .set(NOTION_CREDENTIALS.FILTER_CONDITIONS, mapToJsonb(credential.getFilterConditions()))
                .set(NOTION_CREDENTIALS.IS_ACTIVE, credential.isActive())
                .set(NOTION_CREDENTIALS.CREATED_AT, credential.getCreatedAt())
                .set(NOTION_CREDENTIALS.UPDATED_AT, credential.getUpdatedAt())
                .returning()
                .fetchOne();
        
        return mapToEntity(record);
    }

    private NotionCredential update(NotionCredential credential) {
        var record = dsl.update(NOTION_CREDENTIALS)
                .set(NOTION_CREDENTIALS.API_KEY, credential.getApiKey())
                .set(NOTION_CREDENTIALS.DATABASE_ID, credential.getDatabaseId())
                .set(NOTION_CREDENTIALS.TITLE_PROPERTY, credential.getTitleProperty())
                .set(NOTION_CREDENTIALS.STATUS_PROPERTY, credential.getStatusProperty())
                .set(NOTION_CREDENTIALS.DATE_PROPERTY, credential.getDateProperty())
                .set(NOTION_CREDENTIALS.FILTER_CONDITIONS, mapToJsonb(credential.getFilterConditions()))
                .set(NOTION_CREDENTIALS.IS_ACTIVE, credential.isActive())
                .set(NOTION_CREDENTIALS.UPDATED_AT, credential.getUpdatedAt())
                .where(NOTION_CREDENTIALS.ID.eq(credential.getId()))
                .returning()
                .fetchOne();
        
        return mapToEntity(record);
    }

    private NotionCredential mapToEntity(org.jooq.Record record) {
        JSONB filterConditionsJsonb = record.get(NOTION_CREDENTIALS.FILTER_CONDITIONS);
        Map<String, Object> filterConditions = null;
        
        if (filterConditionsJsonb != null) {
            try {
                filterConditions = objectMapper.readValue(filterConditionsJsonb.data(), 
                    new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                // Log error and continue with null
            }
        }
        
        return NotionCredential.builder()
                .id(record.get(NOTION_CREDENTIALS.ID))
                .userId(record.get(NOTION_CREDENTIALS.USER_ID))
                .apiKey(record.get(NOTION_CREDENTIALS.API_KEY))
                .databaseId(record.get(NOTION_CREDENTIALS.DATABASE_ID))
                .titleProperty(record.get(NOTION_CREDENTIALS.TITLE_PROPERTY))
                .statusProperty(record.get(NOTION_CREDENTIALS.STATUS_PROPERTY))
                .dateProperty(record.get(NOTION_CREDENTIALS.DATE_PROPERTY))
                .filterConditions(filterConditions)
                .isActive(record.get(NOTION_CREDENTIALS.IS_ACTIVE))
                .createdAt(record.get(NOTION_CREDENTIALS.CREATED_AT))
                .updatedAt(record.get(NOTION_CREDENTIALS.UPDATED_AT))
                .build();
    }

    private JSONB mapToJsonb(Map<String, Object> filterConditions) {
        if (filterConditions == null) {
            return null;
        }
        
        try {
            String json = objectMapper.writeValueAsString(filterConditions);
            return JSONB.valueOf(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}