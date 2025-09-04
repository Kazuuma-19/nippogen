package com.example.backend.infrastructure.github;

import com.example.backend.domain.credentials.github.GitHubCredential;
import com.example.backend.infrastructure.github.dto.GitHubCommitDto;
import com.example.backend.infrastructure.github.dto.GitHubRepositoryDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class GitHubApiService {
    
    private final WebClient webClient;
    
    public GitHubApiService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
            .defaultHeader(HttpHeaders.USER_AGENT, "Nippogen-Backend/1.0")
            .build();
    }
    
    /**
     * GitHubリポジトリの存在確認とアクセステスト
     * 
     * @param credential GitHub認証情報
     * @return 接続成功時true
     */
    public boolean testConnection(GitHubCredential credential) {
        try {
            GitHubRepositoryDto repository = webClient
                .get()
                .uri("/repos/{owner}/{repo}", credential.getOwner(), credential.getRepo())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + credential.getApiKey())
                .retrieve()
                .bodyToMono(GitHubRepositoryDto.class)
                .block();
                
            return repository != null;
            
        } catch (WebClientResponseException e) {
            log.error("GitHub接続テスト失敗: owner={}, repo={}, status={}, message={}", 
                credential.getOwner(), credential.getRepo(), e.getStatusCode(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("GitHub接続テスト中に予期しないエラー: owner={}, repo={}", 
                credential.getOwner(), credential.getRepo(), e);
            return false;
        }
    }
    
    /**
     * 指定日のコミット履歴を取得
     * 
     * @param credential GitHub認証情報
     * @param date 対象日
     * @return コミット履歴
     */
    public List<GitHubCommitDto> getCommitsByDate(GitHubCredential credential, LocalDate date) {
        try {
            String since = date.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
            String until = date.plusDays(1).atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
            
            List<GitHubCommitDto> commits = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/repos/{owner}/{repo}/commits")
                    .queryParam("since", since)
                    .queryParam("until", until)
                    .queryParam("author", credential.getOwner()) // 自分のコミットのみ
                    .build(credential.getOwner(), credential.getRepo()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + credential.getApiKey())
                .retrieve()
                .bodyToFlux(GitHubCommitDto.class)
                .collectList()
                .block();
                
            log.info("GitHub APIから{}件のコミットを取得: date={}, owner={}, repo={}", 
                commits != null ? commits.size() : 0, date, credential.getOwner(), credential.getRepo());
                
            return commits != null ? commits : List.of();
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error("GitHub API認証エラー: APIキーが無効または期限切れの可能性があります");
                throw new RuntimeException("GitHub認証に失敗しました。APIキーを確認してください。", e);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.error("GitHub API制限エラー: レート制限または権限不足");
                throw new RuntimeException("GitHub APIへのアクセスが制限されています。", e);
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error("GitHub リポジトリが見つかりません: owner={}, repo={}", 
                    credential.getOwner(), credential.getRepo());
                throw new RuntimeException("指定されたGitHubリポジトリが見つかりません。", e);
            }
            
            log.error("GitHub API呼び出しエラー: status={}, message={}", 
                e.getStatusCode(), e.getMessage());
            throw new RuntimeException("GitHub APIの呼び出しに失敗しました: " + e.getMessage(), e);
            
        } catch (Exception e) {
            log.error("GitHub API呼び出し中に予期しないエラーが発生", e);
            throw new RuntimeException("GitHubデータの取得中にエラーが発生しました。", e);
        }
    }
    
    /**
     * 指定期間のコミット統計を取得
     * 
     * @param credential GitHub認証情報  
     * @param startDate 開始日
     * @param endDate 終了日
     * @return コミット統計
     */
    public GitHubCommitStats getCommitStats(GitHubCredential credential, LocalDate startDate, LocalDate endDate) {
        try {
            String since = startDate.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
            String until = endDate.plusDays(1).atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
            
            List<GitHubCommitDto> commits = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/repos/{owner}/{repo}/commits")
                    .queryParam("since", since)
                    .queryParam("until", until)
                    .queryParam("author", credential.getOwner())
                    .build(credential.getOwner(), credential.getRepo()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + credential.getApiKey())
                .retrieve()
                .bodyToFlux(GitHubCommitDto.class)
                .collectList()
                .block();
            
            if (commits == null) {
                return new GitHubCommitStats(0, 0, 0, List.of());
            }
            
            int totalCommits = commits.size();
            int totalAdditions = commits.stream()
                .mapToInt(commit -> commit.getStats() != null ? commit.getStats().getAdditions() : 0)
                .sum();
            int totalDeletions = commits.stream()
                .mapToInt(commit -> commit.getStats() != null ? commit.getStats().getDeletions() : 0)
                .sum();
                
            return new GitHubCommitStats(totalCommits, totalAdditions, totalDeletions, commits);
            
        } catch (Exception e) {
            log.error("GitHub統計データ取得エラー", e);
            throw new RuntimeException("GitHubの統計データ取得に失敗しました。", e);
        }
    }
    
    /**
     * コミット統計データクラス
     */
    public record GitHubCommitStats(
        int totalCommits,
        int totalAdditions,
        int totalDeletions,
        List<GitHubCommitDto> commits
    ) {}
}