package com.example.backend.infrastructure.repositories.credentials.github;

import static com.example.backend.jooq.tables.JGithubCredentials.GITHUB_CREDENTIALS;

import com.example.backend.domain.credentials.github.GitHubCredential;
import com.example.backend.domain.credentials.github.IGitHubCredentialRepository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GitHubCredentialRepository implements IGitHubCredentialRepository {
    
    private final DSLContext dsl;

    @Override
    public boolean testConnection(String owner, String repo) {
        // TODO: 実際のGitHub API接続テストを実装
        return true;
    }

    @Override
    public GitHubCredential save(GitHubCredential credential) {
        if (existsById(credential.getId())) {
            return update(credential);
        } else {
            return insert(credential);
        }
    }


    @Override
    public List<GitHubCredential> findAllByUserId(UUID userId) {
        return dsl.selectFrom(GITHUB_CREDENTIALS)
                .where(GITHUB_CREDENTIALS.USER_ID.eq(userId))
                .orderBy(GITHUB_CREDENTIALS.CREATED_AT.desc())
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<GitHubCredential> findActiveByUserId(UUID userId) {
        return dsl.selectFrom(GITHUB_CREDENTIALS)
                .where(GITHUB_CREDENTIALS.USER_ID.eq(userId)
                       .and(GITHUB_CREDENTIALS.IS_ACTIVE.eq(true)))
                .orderBy(GITHUB_CREDENTIALS.CREATED_AT.desc())
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public boolean existsById(UUID id) {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(GITHUB_CREDENTIALS)
                .where(GITHUB_CREDENTIALS.ID.eq(id))
        );
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return dsl.fetchExists(
            dsl.selectOne()
                .from(GITHUB_CREDENTIALS)
                .where(GITHUB_CREDENTIALS.USER_ID.eq(userId)
                       .and(GITHUB_CREDENTIALS.IS_ACTIVE.eq(true)))
        );
    }

    @Override
    public void deleteById(UUID id) {
        dsl.deleteFrom(GITHUB_CREDENTIALS)
                .where(GITHUB_CREDENTIALS.ID.eq(id))
                .execute();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        dsl.deleteFrom(GITHUB_CREDENTIALS)
                .where(GITHUB_CREDENTIALS.USER_ID.eq(userId))
                .execute();
    }

    @Override
    public long countByUserId(UUID userId) {
        return dsl.selectCount()
                .from(GITHUB_CREDENTIALS)
                .where(GITHUB_CREDENTIALS.USER_ID.eq(userId)
                       .and(GITHUB_CREDENTIALS.IS_ACTIVE.eq(true)))
                .fetchOne(0, Long.class);
    }

    private GitHubCredential insert(GitHubCredential credential) {
        var record = dsl.insertInto(GITHUB_CREDENTIALS)
                .set(GITHUB_CREDENTIALS.ID, credential.getId())
                .set(GITHUB_CREDENTIALS.USER_ID, credential.getUserId())
                .set(GITHUB_CREDENTIALS.API_KEY, credential.getApiKey())
                .set(GITHUB_CREDENTIALS.BASE_URL, credential.getBaseUrl())
                .set(GITHUB_CREDENTIALS.OWNER, credential.getOwner())
                .set(GITHUB_CREDENTIALS.REPO, credential.getRepo())
                .set(GITHUB_CREDENTIALS.IS_ACTIVE, credential.isActive())
                .set(GITHUB_CREDENTIALS.CREATED_AT, credential.getCreatedAt())
                .set(GITHUB_CREDENTIALS.UPDATED_AT, credential.getUpdatedAt())
                .returning()
                .fetchOne();
        
        return mapToEntity(record);
    }

    private GitHubCredential update(GitHubCredential credential) {
        var record = dsl.update(GITHUB_CREDENTIALS)
                .set(GITHUB_CREDENTIALS.API_KEY, credential.getApiKey())
                .set(GITHUB_CREDENTIALS.BASE_URL, credential.getBaseUrl())
                .set(GITHUB_CREDENTIALS.OWNER, credential.getOwner())
                .set(GITHUB_CREDENTIALS.REPO, credential.getRepo())
                .set(GITHUB_CREDENTIALS.IS_ACTIVE, credential.isActive())
                .set(GITHUB_CREDENTIALS.UPDATED_AT, credential.getUpdatedAt())
                .where(GITHUB_CREDENTIALS.ID.eq(credential.getId()))
                .returning()
                .fetchOne();
        
        return mapToEntity(record);
    }

    private GitHubCredential mapToEntity(org.jooq.Record record) {
        return GitHubCredential.builder()
                .id(record.get(GITHUB_CREDENTIALS.ID))
                .userId(record.get(GITHUB_CREDENTIALS.USER_ID))
                .apiKey(record.get(GITHUB_CREDENTIALS.API_KEY))
                .baseUrl(record.get(GITHUB_CREDENTIALS.BASE_URL))
                .owner(record.get(GITHUB_CREDENTIALS.OWNER))
                .repo(record.get(GITHUB_CREDENTIALS.REPO))
                .isActive(record.get(GITHUB_CREDENTIALS.IS_ACTIVE))
                .createdAt(record.get(GITHUB_CREDENTIALS.CREATED_AT))
                .updatedAt(record.get(GITHUB_CREDENTIALS.UPDATED_AT))
                .build();
    }
}
