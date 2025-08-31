package com.example.backend.application.usecases.external.github;

import com.example.backend.application.dto.external.github.CommitDto;
import com.example.backend.application.dto.external.github.PullRequestDto;
import com.example.backend.domain.external.github.GitHubCommit;
import com.example.backend.domain.external.github.PullRequest;
import com.example.backend.domain.external.github.IGitHubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GitHub統合ユースケース
 * プレゼンテーション層からの要求を処理し、ドメインサービスを呼び出す
 */
@Service
@RequiredArgsConstructor
public class GitHubUseCase {
    
    private final IGitHubRepository IGitHubRepository;
    
    /**
     * GitHub接続テスト
     * 
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @return 接続成功時true
     */
    public boolean testConnection(String owner, String repo) {
        return IGitHubRepository.testConnection(owner, repo);
    }
    
    /**
     * 指定日のプルリクエスト情報を取得
     * 
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @param date 対象日
     * @return プルリクエストDTO一覧
     */
    public List<PullRequestDto> getPullRequestsForDate(String owner, String repo, LocalDate date) {
        
        List<PullRequest> pullRequests = IGitHubRepository.findPullRequestsByDate(owner, repo, date);
        
        return pullRequests.stream()
                .map(this::toPullRequestDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 指定日のコミット情報を取得
     * 
     * @param owner リポジトリオーナー
     * @param repo リポジトリ名
     * @param date 対象日
     * @return コミットDTO一覧
     */
    public List<CommitDto> getCommitsForDate(String owner, String repo, LocalDate date) {
        
        List<GitHubCommit> commits = IGitHubRepository.findCommitsByDate(owner, repo, date);
        
        return commits.stream()
                .map(this::toCommitDto)
                .collect(Collectors.toList());
    }
    
    /**
     * PullRequestエンティティをPullRequestDTOに変換
     * 
     * @param pullRequest プルリクエストエンティティ
     * @return プルリクエストDTO
     */
    private PullRequestDto toPullRequestDto(PullRequest pullRequest) {
        return PullRequestDto.builder()
                .id(pullRequest.getId())
                .number(pullRequest.getNumber())
                .title(pullRequest.getTitle())
                .body(pullRequest.getBody())
                .state(pullRequest.getState())
                .author(pullRequest.getAuthor())
                .createdAt(pullRequest.getCreatedAt())
                .updatedAt(pullRequest.getUpdatedAt())
                .mergedAt(pullRequest.getMergedAt())
                .baseBranch(pullRequest.getBaseBranch())
                .headBranch(pullRequest.getHeadBranch())
                .reviewers(pullRequest.getReviewers())
                .additions(pullRequest.getAdditions())
                .deletions(pullRequest.getDeletions())
                .changedFiles(pullRequest.getChangedFiles())
                .htmlUrl(pullRequest.getHtmlUrl())
                .build();
    }
    
    /**
     * GitHubCommitエンティティをCommitDTOに変換
     * 
     * @param commit コミットエンティティ
     * @return コミットDTO
     */
    private CommitDto toCommitDto(GitHubCommit commit) {
        return CommitDto.builder()
                .sha(commit.getSha())
                .message(commit.getMessage())
                .author(commit.getAuthor())
                .authorEmail(commit.getAuthorEmail())
                .date(commit.getDate())
                .additions(commit.getAdditions())
                .deletions(commit.getDeletions())
                .total(commit.getTotal())
                .htmlUrl(commit.getHtmlUrl())
                .build();
    }
}
