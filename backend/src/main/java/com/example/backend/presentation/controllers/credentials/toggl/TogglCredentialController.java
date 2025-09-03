package com.example.backend.presentation.controllers.credentials.toggl;

import com.example.backend.application.dto.credentials.toggl.TogglCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.toggl.TogglCredentialResponseDto;
import com.example.backend.application.usecases.credentials.toggl.TogglCredentialUseCase;
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
@RequestMapping("/api/credentials/toggl")
@RequiredArgsConstructor
@Tag(name = "Toggl認証情報", description = "Toggl Track API認証情報の管理")
public class TogglCredentialController {
    
    private final TogglCredentialUseCase togglCredentialUseCase;
    
    @PostMapping
    @Operation(summary = "Toggl認証情報作成", description = "新しいToggl認証情報を作成します")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "認証情報の作成に成功")
    })
    @CommonApiResponses.CredentialErrorResponses
    public ResponseEntity<TogglCredentialResponseDto> create(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody TogglCredentialCreateRequestDto request) {
        
        TogglCredentialResponseDto response = togglCredentialUseCase.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    
    @GetMapping("/all")
    @Operation(summary = "ユーザーの全Toggl認証情報取得", description = "指定されたユーザーの全Toggl認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功")
    })
    @CommonApiResponses.CredentialErrorResponses
    public ResponseEntity<List<TogglCredentialResponseDto>> findAllByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        List<TogglCredentialResponseDto> credentials = togglCredentialUseCase.findAllByUserId(userId);
        return ResponseEntity.ok(credentials);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Toggl認証情報削除", description = "指定されたIDのToggl認証情報を削除します")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "認証情報の削除に成功")
    })
    @CommonApiResponses.CredentialDeleteErrorResponses
    public ResponseEntity<Void> delete(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        try {
            togglCredentialUseCase.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/test")
    @Operation(summary = "ToggleTrack接続テスト", description = "ToggleTrack API接続をテストして、アクセス権限を確認します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "接続テストが正常に完了")
    })
    @CommonApiResponses.CredentialErrorResponses
    public ResponseEntity<Boolean> testConnection() {
        boolean isConnected = togglCredentialUseCase.testConnection();
        return ResponseEntity.ok(isConnected);
    }
}