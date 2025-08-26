package com.example.backend.infrastructure.github.client;

import com.example.backend.infrastructure.github.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class GitHubRestApiClient {

    private final WebClient gitHubWebClient;

    @Autowired
    public GitHubRestApiClient(WebClient gitHubWebClient) {
        this.gitHubWebClient = gitHubWebClient;
    }

    public Mono<GitHubRepositoryDto> getRepository(String owner, String repo) {
        log.info("Fetching repository: {}/{}", owner, repo);
        
        return gitHubWebClient
                .get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .bodyToMono(GitHubRepositoryDto.class)
                .doOnSuccess(repository -> log.info("Successfully fetched repository: {}", repository.fullName()))
                .doOnError(error -> log.error("Failed to fetch repository {}/{}: {}", owner, repo, error.getMessage()));
    }

    public Mono<GitHubSearchResultDto<GitHubPullRequestDto>> searchPullRequests(String owner, String repo, LocalDate date) {
        log.info("Searching PRs for {}/{} on {}", owner, repo, date);
        
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String query = String.format("repo:%s/%s type:pr created:%s", owner, repo, dateStr);
        
        return gitHubWebClient
                .get()
                .uri("/search/issues?q={query}&sort=created&order=desc", query)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GitHubSearchResultDto<GitHubPullRequestDto>>() {})
                .doOnSuccess(result -> log.info("Found {} PRs for {}/{} on {}", result.totalCount(), owner, repo, date))
                .doOnError(error -> log.error("Failed to search PRs for {}/{} on {}: {}", owner, repo, date, error.getMessage()));
    }

    public Mono<List<GitHubCommitDto>> getCommits(String owner, String repo, LocalDate date) {
        log.info("Fetching commits for {}/{} on {}", owner, repo, date);
        
        String since = date.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
        String until = date.plusDays(1).atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
        
        return gitHubWebClient
                .get()
                .uri("/repos/{owner}/{repo}/commits?since={since}&until={until}&per_page=100", 
                     owner, repo, since, until)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GitHubCommitDto>>() {})
                .doOnSuccess(commits -> log.info("Found {} commits for {}/{} on {}", commits.size(), owner, repo, date))
                .doOnError(error -> log.error("Failed to fetch commits for {}/{} on {}: {}", owner, repo, date, error.getMessage()));
    }
}