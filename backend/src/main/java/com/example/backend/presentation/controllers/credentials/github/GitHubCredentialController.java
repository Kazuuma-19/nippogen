package com.example.backend.presentation.controllers.credentials.github;

import com.example.backend.application.dto.credentials.github.GitHubCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.github.GitHubCredentialResponseDto;
import com.example.backend.application.dto.credentials.github.GitHubCredentialUpdateRequestDto;
import com.example.backend.application.usecases.credentials.github.GitHubCredentialUseCase;
import com.example.backend.application.usecases.external.github.GitHubUseCase;

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
    private final GitHubUseCase gitHubUseCase;
    
    @PostMapping
    @Operation(summary = "GitHub認証情報作成", description = "新しいGitHub認証情報を作成します")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "認証情報の作成に成功"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<GitHubCredentialResponseDto> create(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody GitHubCredentialCreateRequestDto request) {
        
        GitHubCredentialResponseDto response = gitHubCredentialUseCase.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "GitHub認証情報取得", description = "指定されたIDのGitHub認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<GitHubCredentialResponseDto> findById(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        return gitHubCredentialUseCase.findById(id)
                .map(credential -> ResponseEntity.ok(credential))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "ユーザーのGitHub認証情報取得", description = "指定されたユーザーのアクティブなGitHub認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<GitHubCredentialResponseDto> findByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        return gitHubCredentialUseCase.findByUserId(userId)
                .map(credential -> ResponseEntity.ok(credential))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/all")
    @Operation(summary = "ユーザーの全GitHub認証情報取得", description = "指定されたユーザーの全GitHub認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<List<GitHubCredentialResponseDto>> findAllByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        List<GitHubCredentialResponseDto> credentials = gitHubCredentialUseCase.findAllByUserId(userId);
        return ResponseEntity.ok(credentials);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "GitHub認証情報更新", description = "指定されたIDのGitHub認証情報を更新します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の更新に成功"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<GitHubCredentialResponseDto> update(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id,
            @RequestBody GitHubCredentialUpdateRequestDto request) {
        
        try {
            GitHubCredentialResponseDto response = gitHubCredentialUseCase.update(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "GitHub認証情報削除", description = "指定されたIDのGitHub認証情報を削除します")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "認証情報の削除に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        try {
            gitHubCredentialUseCase.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/exists")
    @Operation(summary = "GitHub認証情報存在確認", description = "指定されたユーザーにGitHub認証情報が存在するかチェックします")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "存在確認の結果を返却"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<Boolean> exists(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        boolean exists = gitHubCredentialUseCase.existsByUserId(userId);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/test")
    @Operation(summary = "GitHub接続テスト", description = "GitHub リポジトリ接続をテストして、アクセス権限とリポジトリの存在を確認します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "接続テストが正常に完了"),
        @ApiResponse(responseCode = "400", description = "リクエストパラメータが無効（owner または repo が不足）"),
        @ApiResponse(responseCode = "500", description = "内部サーバーエラーまたは接続失敗")
    })
    public ResponseEntity<Boolean> testConnection(
            @Parameter(description = "リポジトリオーナー/組織名", example = "octocat", required = true) 
            @RequestParam String owner,
            @Parameter(description = "リポジトリ名", example = "Hello-World", required = true) 
            @RequestParam String repo) {
        
        boolean isConnected = gitHubUseCase.testConnection(owner, repo);
        return ResponseEntity.ok(isConnected);
    }
}