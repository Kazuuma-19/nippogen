package com.example.backend.infrastructure.repositories.credentials.toggl;

import static com.example.backend.jooq.tables.JTogglCredentials.TOGGL_CREDENTIALS;

import com.example.backend.domain.credentials.toggl.TogglCredential;
import com.example.backend.domain.credentials.toggl.ITogglCredentialRepository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TogglCredentialRepository implements ITogglCredentialRepository {
    
    private final DSLContext dsl;

    @Override
    public TogglCredential save(TogglCredential credential) {
        if (existsById(credential.getId())) {
            return update(credential);
        } else {
            return insert(credential);
        }
    }


    @Override
    public List<TogglCredential> findAllByUserId(UUID userId) {
        return dsl.selectFrom(TOGGL_CREDENTIALS)
                .where(TOGGL_CREDENTIALS.USER_ID.eq(userId))
                .orderBy(TOGGL_CREDENTIALS.CREATED_AT.desc())
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<TogglCredential> findActiveByUserId(UUID userId) {
        return dsl.selectFrom(TOGGL_CREDENTIALS)
                .where(TOGGL_CREDENTIALS.USER_ID.eq(userId)
                       .and(TOGGL_CREDENTIALS.IS_ACTIVE.eq(true)))
                .orderBy(TOGGL_CREDENTIALS.CREATED_AT.desc())
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public boolean existsById(UUID id) {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(TOGGL_CREDENTIALS)
                .where(TOGGL_CREDENTIALS.ID.eq(id))
        );
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(TOGGL_CREDENTIALS)
                .where(TOGGL_CREDENTIALS.USER_ID.eq(userId)
                       .and(TOGGL_CREDENTIALS.IS_ACTIVE.eq(true)))
        );
    }

    @Override
    public void deleteById(UUID id) {
        dsl.deleteFrom(TOGGL_CREDENTIALS)
                .where(TOGGL_CREDENTIALS.ID.eq(id))
                .execute();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        dsl.deleteFrom(TOGGL_CREDENTIALS)
                .where(TOGGL_CREDENTIALS.USER_ID.eq(userId))
                .execute();
    }

    @Override
    public long countByUserId(UUID userId) {
        return dsl.selectCount()
                .from(TOGGL_CREDENTIALS)
                .where(TOGGL_CREDENTIALS.USER_ID.eq(userId)
                       .and(TOGGL_CREDENTIALS.IS_ACTIVE.eq(true)))
                .fetchOne(0, Long.class);
    }
    
    @Override
    public boolean testConnection() {
        // TODO: 実際のToggleTrack API接続テストを実装
        return true;
    }

    private TogglCredential insert(TogglCredential credential) {
        var record = dsl.insertInto(TOGGL_CREDENTIALS)
                .set(TOGGL_CREDENTIALS.ID, credential.getId())
                .set(TOGGL_CREDENTIALS.USER_ID, credential.getUserId())
                .set(TOGGL_CREDENTIALS.API_KEY, credential.getApiKey())
                .set(TOGGL_CREDENTIALS.WORKSPACE_ID, credential.getWorkspaceId())
                .set(TOGGL_CREDENTIALS.PROJECT_IDS, credential.getProjectIds() != null ? 
                    credential.getProjectIds().toArray(new Integer[0]) : null)
                .set(TOGGL_CREDENTIALS.DEFAULT_TAGS, credential.getDefaultTags() != null ? 
                    credential.getDefaultTags().toArray(new String[0]) : null)
                .set(TOGGL_CREDENTIALS.TIME_ZONE, credential.getTimeZone())
                .set(TOGGL_CREDENTIALS.INCLUDE_WEEKENDS, credential.isIncludeWeekends())
                .set(TOGGL_CREDENTIALS.IS_ACTIVE, credential.isActive())
                .set(TOGGL_CREDENTIALS.CREATED_AT, credential.getCreatedAt())
                .set(TOGGL_CREDENTIALS.UPDATED_AT, credential.getUpdatedAt())
                .returning()
                .fetchOne();
        
        return mapToEntity(record);
    }

    private TogglCredential update(TogglCredential credential) {
        var record = dsl.update(TOGGL_CREDENTIALS)
                .set(TOGGL_CREDENTIALS.API_KEY, credential.getApiKey())
                .set(TOGGL_CREDENTIALS.WORKSPACE_ID, credential.getWorkspaceId())
                .set(TOGGL_CREDENTIALS.PROJECT_IDS, credential.getProjectIds() != null ? 
                    credential.getProjectIds().toArray(new Integer[0]) : null)
                .set(TOGGL_CREDENTIALS.DEFAULT_TAGS, credential.getDefaultTags() != null ? 
                    credential.getDefaultTags().toArray(new String[0]) : null)
                .set(TOGGL_CREDENTIALS.TIME_ZONE, credential.getTimeZone())
                .set(TOGGL_CREDENTIALS.INCLUDE_WEEKENDS, credential.isIncludeWeekends())
                .set(TOGGL_CREDENTIALS.IS_ACTIVE, credential.isActive())
                .set(TOGGL_CREDENTIALS.UPDATED_AT, credential.getUpdatedAt())
                .where(TOGGL_CREDENTIALS.ID.eq(credential.getId()))
                .returning()
                .fetchOne();
        
        return mapToEntity(record);
    }

    private TogglCredential mapToEntity(org.jooq.Record record) {
        Integer[] projectIdsArray = record.get(TOGGL_CREDENTIALS.PROJECT_IDS);
        String[] defaultTagsArray = record.get(TOGGL_CREDENTIALS.DEFAULT_TAGS);
        
        return TogglCredential.builder()
                .id(record.get(TOGGL_CREDENTIALS.ID))
                .userId(record.get(TOGGL_CREDENTIALS.USER_ID))
                .apiKey(record.get(TOGGL_CREDENTIALS.API_KEY))
                .workspaceId(record.get(TOGGL_CREDENTIALS.WORKSPACE_ID))
                .projectIds(projectIdsArray != null ? Arrays.asList(projectIdsArray) : null)
                .defaultTags(defaultTagsArray != null ? Arrays.asList(defaultTagsArray) : null)
                .timeZone(record.get(TOGGL_CREDENTIALS.TIME_ZONE))
                .includeWeekends(record.get(TOGGL_CREDENTIALS.INCLUDE_WEEKENDS))
                .isActive(record.get(TOGGL_CREDENTIALS.IS_ACTIVE))
                .createdAt(record.get(TOGGL_CREDENTIALS.CREATED_AT))
                .updatedAt(record.get(TOGGL_CREDENTIALS.UPDATED_AT))
                .build();
    }
}
