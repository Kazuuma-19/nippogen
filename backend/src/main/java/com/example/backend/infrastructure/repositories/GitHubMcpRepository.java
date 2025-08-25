package com.example.backend.service;

import com.example.backend.config.McpConfig;
import com.example.backend.dto.CommitDto;
import com.example.backend.dto.PullRequestDto;
import com.example.backend.exception.GitHubMcpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubMcpService {
    
    private final McpConfig mcpConfig;
    
    /**
     * GitHub公式MCP Server経由で接続をテスト
     * 使用可能なツール: search_repositories, get_repository, list_issues等
     */
    public boolean testConnection(String owner, String repo) {
        try {
            log.info("Testing GitHub MCP connection for {}/{}", owner, repo);
            
            // GitHub MCP Serverのget_repositoryツールを使用してリポジトリ情報を取得
            String repositoryName = owner + "/" + repo;
            
            // TODO: 実際のMCP通信実装
            // MCPクライアントを使用してget_repositoryツールを呼び出し
            // ProcessBuilder or 他のMCPクライアントライブラリを使用
            
            log.info("GitHub MCP connection test successful for {}", repositoryName);
            return true;
            
        } catch (Exception e) {
            log.error("GitHub MCP connection failed for {}/{}: {}", owner, repo, e.getMessage());
            throw GitHubMcpException.connectionFailed(e.getMessage());
        }
    }
    
    /**
     * GitHub公式MCP Server経由で指定日のPR情報を取得
     * 使用ツール: search_issues (type:pr)
     */
    public List<PullRequestDto> getPullRequestsForDate(String owner, String repo, LocalDate date) {
        try {
            log.info("Fetching PRs via GitHub MCP for {}/{} on {}", owner, repo, date);
            
            String repositoryName = owner + "/" + repo;
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // GitHub MCP Serverのsearch_issuesツールを使用
            // クエリ例: "repo:owner/repo type:pr created:2024-01-15"
            String query = String.format("repo:%s type:pr created:%s", repositoryName, dateStr);
            
            // TODO: 実際のMCP通信実装
            // MCPクライアントでsearch_issuesツールを呼び出し
            // レスポンスをPullRequestDtoに変換
            
            List<PullRequestDto> pullRequests = new ArrayList<>();
            // サンプル実装（実際のMCP通信後に置き換え）
            
            log.info("Found {} PRs for {}/{} on {}", pullRequests.size(), owner, repo, date);
            return pullRequests;
            
        } catch (Exception e) {
            log.error("Failed to fetch PRs for {}/{} on {}: {}", owner, repo, date, e.getMessage());
            throw GitHubMcpException.connectionFailed("Failed to fetch PRs: " + e.getMessage());
        }
    }
    
    /**
     * GitHub公式MCP Server経由で指定日のコミット情報を取得
     * 使用ツール: get_repository (commits endpoint)
     */
    public List<CommitDto> getCommitsForDate(String owner, String repo, LocalDate date) {
        try {
            log.info("Fetching commits via GitHub MCP for {}/{} on {}", owner, repo, date);
            
            String repositoryName = owner + "/" + repo;
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // GitHub MCP ServerでリポジトリのコミットAPIを使用
            // since/untilパラメータで日付範囲を指定
            
            // TODO: 実際のMCP通信実装
            // MCPクライアントでget_repository (commits)を呼び出し
            // レスポンスをCommitDtoに変換
            
            List<CommitDto> commits = new ArrayList<>();
            // サンプル実装（実際のMCP通信後に置き換え）
            
            log.info("Found {} commits for {}/{} on {}", commits.size(), owner, repo, date);
            return commits;
            
        } catch (Exception e) {
            log.error("Failed to fetch commits for {}/{} on {}: {}", owner, repo, date, e.getMessage());
            throw GitHubMcpException.connectionFailed("Failed to fetch commits: " + e.getMessage());
        }
    }
}