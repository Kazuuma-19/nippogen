package com.example.backend.infrastructure.repositories;

import com.example.backend.domain.entities.GitHubCommit;
import com.example.backend.domain.entities.PullRequest;
import com.example.backend.domain.repositories.GitHubRepository;
import com.example.backend.infrastructure.github.client.GitHubRestApiClient;
import com.example.backend.infrastructure.github.dto.GitHubCommitDto;
import com.example.backend.infrastructure.github.dto.GitHubPullRequestDto;
import com.example.backend.infrastructure.github.dto.GitHubSearchResultDto;
import com.example.backend.shared.exceptions.GitHubMcpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class GitHubRestApiRepository implements GitHubRepository {

    private final GitHubRestApiClient gitHubApiClient;

    @Autowired
    public GitHubRestApiRepository(GitHubRestApiClient gitHubApiClient) {
        this.gitHubApiClient = gitHubApiClient;
    }
    
    /**
     * GitHub REST API経由で接続をテスト
     * リポジトリ情報を取得してアクセス可能性を確認
     */
    @Override
    public boolean testConnection(String owner, String repo) {
        try {
            log.info("Testing GitHub REST API connection for {}/{}", owner, repo);
            
            return gitHubApiClient.getRepository(owner, repo)
                    .map(repository -> {
                        log.info("GitHub REST API connection test successful for {}", repository.fullName());
                        return true;
                    })
                    .onErrorMap(error -> {
                        log.error("GitHub REST API connection failed for {}/{}: {}", owner, repo, error.getMessage());
                        return GitHubMcpException.connectionFailed(error.getMessage());
                    })
                    .block();
                    
        } catch (Exception e) {
            log.error("GitHub REST API connection failed for {}/{}: {}", owner, repo, e.getMessage());
            throw GitHubMcpException.connectionFailed(e.getMessage());
        }
    }
    
    /**
     * GitHub REST API経由で指定日のPR情報を取得
     * Search Issues APIを使用してtype:prで絞り込み
     */
    @Override
    public List<PullRequest> findPullRequestsByDate(String owner, String repo, LocalDate date) {
        try {
            log.info("Fetching PRs via GitHub REST API for {}/{} on {}", owner, repo, date);
            
            return gitHubApiClient.searchPullRequests(owner, repo, date)
                    .map(searchResult -> convertToPullRequests(searchResult, owner, repo))
                    .onErrorMap(error -> {
                        log.error("Failed to fetch PRs for {}/{} on {}: {}", owner, repo, date, error.getMessage());
                        return GitHubMcpException.connectionFailed("Failed to fetch PRs: " + error.getMessage());
                    })
                    .block();
                    
        } catch (Exception e) {
            log.error("Failed to fetch PRs for {}/{} on {}: {}", owner, repo, date, e.getMessage());
            throw GitHubMcpException.connectionFailed("Failed to fetch PRs: " + e.getMessage());
        }
    }
    
    /**
     * GitHub REST API経由で指定日のコミット情報を取得
     * Commits APIを使用してsince/untilパラメータで日付フィルタ
     */
    @Override
    public List<GitHubCommit> findCommitsByDate(String owner, String repo, LocalDate date) {
        try {
            log.info("Fetching commits via GitHub REST API for {}/{} on {}", owner, repo, date);
            
            return gitHubApiClient.getCommits(owner, repo, date)
                    .map(commitDtos -> convertToCommits(commitDtos, owner, repo))
                    .onErrorMap(error -> {
                        log.error("Failed to fetch commits for {}/{} on {}: {}", owner, repo, date, error.getMessage());
                        return GitHubMcpException.connectionFailed("Failed to fetch commits: " + error.getMessage());
                    })
                    .block();
                    
        } catch (Exception e) {
            log.error("Failed to fetch commits for {}/{} on {}: {}", owner, repo, date, e.getMessage());
            throw GitHubMcpException.connectionFailed("Failed to fetch commits: " + e.getMessage());
        }
    }
    
    private List<PullRequest> convertToPullRequests(GitHubSearchResultDto<GitHubPullRequestDto> searchResult, String owner, String repo) {
        return searchResult.items().stream()
                .map(prDto -> PullRequest.builder()
                        .id(prDto.id())
                        .number(prDto.number())
                        .title(prDto.title())
                        .body(prDto.body())
                        .state(prDto.state())
                        .author(prDto.user().login())
                        .createdAt(prDto.createdAt())
                        .updatedAt(prDto.updatedAt())
                        .mergedAt(prDto.mergedAt())
                        .htmlUrl(prDto.htmlUrl())
                        .build())
                .toList();
    }
    
    private List<GitHubCommit> convertToCommits(List<GitHubCommitDto> commitDtos, String owner, String repo) {
        return commitDtos.stream()
                .map(commitDto -> GitHubCommit.builder()
                        .sha(commitDto.sha())
                        .message(commitDto.commit().message())
                        .author(commitDto.commit().author().name())
                        .authorEmail(commitDto.commit().author().email())
                        .date(commitDto.commit().author().date())
                        .htmlUrl(commitDto.htmlUrl())
                        .build())
                .toList();
    }
}