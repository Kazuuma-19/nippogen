package com.example.backend.interfaces.controllers;

import com.example.backend.application.dto.CommitDto;
import com.example.backend.application.dto.PullRequestDto;
import com.example.backend.application.usecases.GitHubUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/external/github")
@Tag(name = "GitHub Integration", description = "GitHub MCP integration endpoints for daily report generation")
@RequiredArgsConstructor
public class GitHubController {
    
    private final GitHubUseCase gitHubUseCase;
    
    @GetMapping("/test")
    @Operation(
        summary = "Test GitHub connection", 
        description = "Test GitHub repository connection via MCP to verify access permissions and repository existence"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Connection test completed successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(type = "boolean", example = "true")
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters (missing owner or repo)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or MCP connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<Boolean> testConnection(
            @Parameter(description = "Repository owner/organization name", example = "octocat", required = true) 
            @RequestParam String owner,
            @Parameter(description = "Repository name", example = "Hello-World", required = true) 
            @RequestParam String repo) {
        
        boolean isConnected = gitHubUseCase.testConnection(owner, repo);
        return ResponseEntity.ok(isConnected);
    }
    
    @GetMapping("/pull-requests")
    @Operation(
        summary = "Get pull requests for date", 
        description = "Retrieve all pull requests created, updated, or merged on the specified date via MCP"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Pull requests retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PullRequestDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters or date format",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Repository not found or access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or MCP connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<List<PullRequestDto>> getPullRequestsForDate(
            @Parameter(description = "Repository owner/organization name", example = "octocat", required = true) 
            @RequestParam String owner,
            @Parameter(description = "Repository name", example = "Hello-World", required = true) 
            @RequestParam String repo,
            @Parameter(
                description = "Target date in ISO format (YYYY-MM-DD)", 
                example = "2024-01-15", 
                required = true
            ) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<PullRequestDto> prs = gitHubUseCase.getPullRequestsForDate(owner, repo, date);
        return ResponseEntity.ok(prs);
    }
    
    @GetMapping("/commits")
    @Operation(
        summary = "Get commits for date", 
        description = "Retrieve all commits made on the specified date via MCP"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Commits retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CommitDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters or date format",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Repository not found or access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or MCP connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<List<CommitDto>> getCommitsForDate(
            @Parameter(description = "Repository owner/organization name", example = "octocat", required = true) 
            @RequestParam String owner,
            @Parameter(description = "Repository name", example = "Hello-World", required = true) 
            @RequestParam String repo,
            @Parameter(
                description = "Target date in ISO format (YYYY-MM-DD)", 
                example = "2024-01-15", 
                required = true
            ) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<CommitDto> commits = gitHubUseCase.getCommitsForDate(owner, repo, date);
        return ResponseEntity.ok(commits);
    }
}