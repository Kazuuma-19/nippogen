package com.example.backend.infrastructure.repositories.external.github;

import com.example.backend.domain.external.github.GitHubCommit;
import com.example.backend.domain.external.github.PullRequest;
import com.example.backend.domain.external.github.IGitHubRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * GitHubRepository の基本実装
 * 将来的にGitHub API連携を実装する際の基盤クラス
 */
@Repository
@Slf4j
public class GitHubRepository implements IGitHubRepository {

    @Override
    public boolean testConnection(String owner, String repo) {
        log.info("Testing connection to {}/{}", owner, repo);
        // TODO: 実際のGitHub API接続テストを実装
        return true;
    }

    @Override
    public List<PullRequest> findPullRequestsByDate(String owner, String repo, LocalDate date) {
        log.info("Finding pull requests for {}/{} on {}", owner, repo, date);
        // TODO: 実際のGitHub API呼び出しを実装
        return Collections.emptyList();
    }

    @Override
    public List<GitHubCommit> findCommitsByDate(String owner, String repo, LocalDate date) {
        log.info("Finding commits for {}/{} on {}", owner, repo, date);
        // TODO: 実際のGitHub API呼び出しを実装
        return Collections.emptyList();
    }
}
