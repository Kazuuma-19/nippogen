package com.example.backend.presentation.controllers.credentials.github;

import com.example.backend.application.dto.credentials.github.GitHubCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.github.GitHubCredentialResponseDto;
import com.example.backend.application.usecases.credentials.github.GitHubCredentialUseCase;
import com.example.backend.common.util.CommonApiResponses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/credentials/github")
@RequiredArgsConstructor
@Tag(name = "GitHub認証情報", description = "GitHub API認証情報の管理")
public class GitHubCredentialController {
    
    private final GitHubCredentialUseCase gitHubCredentialUseCase;
    
    @PostMapping
    @Operation(summary = "GitHub認証情報作成", description = "新しいGitHub認証情報を作成します")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "認証情報の作成に成功")
    })
    @CommonApiResponses.CredentialErrorResponses
    public ResponseEntity<GitHubCredentialResponseDto> create(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody GitHubCredentialCreateRequestDto request) {
        
        GitHubCredentialResponseDto response = gitHubCredentialUseCase.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    
    @GetMapping("/all")
    @Operation(summary = "ユーザーの全GitHub認証情報取得", description = "指定されたユーザーの全GitHub認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功")
    })
    @CommonApiResponses.CredentialErrorResponses
    public ResponseEntity<List<GitHubCredentialResponseDto>> findAllByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        List<GitHubCredentialResponseDto> credentials = gitHubCredentialUseCase.findAllByUserId(userId);
        return ResponseEntity.ok(credentials);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "GitHub認証情報削除", description = "指定されたIDのGitHub認証情報を削除します")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "認証情報の削除に成功")
    })
    @CommonApiResponses.CredentialDeleteErrorResponses
    public ResponseEntity<Void> delete(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        gitHubCredentialUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/test")
    @Operation(summary = "GitHub接続テスト（簡易版）", description = "GitHub リポジトリ接続をテストして、アクセス権限とリポジトリの存在を確認します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "接続テストが正常に完了")
    })
    @CommonApiResponses.CredentialErrorResponses
    public ResponseEntity<Boolean> testConnection(
            @Parameter(description = "リポジトリオーナー/組織名", example = "octocat", required = true) 
            @RequestParam String owner,
            @Parameter(description = "リポジトリ名", example = "Hello-World", required = true) 
            @RequestParam String repo) {
        
        boolean isConnected = gitHubCredentialUseCase.testConnection(owner, repo);
        return ResponseEntity.ok(isConnected);
    }
}