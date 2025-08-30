package com.example.backend.presentation.controllers.credentials.toggle;

import com.example.backend.application.dto.credentials.toggle.ToggleCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.toggle.ToggleCredentialResponseDto;
import com.example.backend.application.dto.credentials.toggle.ToggleCredentialUpdateRequestDto;
import com.example.backend.application.usecases.credentials.toggle.ToggleCredentialUseCase;

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
@RequestMapping("/api/credentials/toggle")
@RequiredArgsConstructor
@Tag(name = "Toggle認証情報", description = "Toggle Track API認証情報の管理")
public class ToggleCredentialController {
    
    private final ToggleCredentialUseCase toggleCredentialUseCase;
    
    @PostMapping
    @Operation(summary = "Toggle認証情報作成", description = "新しいToggle認証情報を作成します")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "認証情報の作成に成功"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<ToggleCredentialResponseDto> create(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody ToggleCredentialCreateRequestDto request) {
        
        ToggleCredentialResponseDto response = toggleCredentialUseCase.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Toggle認証情報取得", description = "指定されたIDのToggle認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<ToggleCredentialResponseDto> findById(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        return toggleCredentialUseCase.findById(id)
                .map(credential -> ResponseEntity.ok(credential))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "ユーザーのToggle認証情報取得", description = "指定されたユーザーのアクティブなToggle認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<ToggleCredentialResponseDto> findByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        return toggleCredentialUseCase.findByUserId(userId)
                .map(credential -> ResponseEntity.ok(credential))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/all")
    @Operation(summary = "ユーザーの全Toggle認証情報取得", description = "指定されたユーザーの全Toggle認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<List<ToggleCredentialResponseDto>> findAllByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        List<ToggleCredentialResponseDto> credentials = toggleCredentialUseCase.findAllByUserId(userId);
        return ResponseEntity.ok(credentials);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Toggle認証情報更新", description = "指定されたIDのToggle認証情報を更新します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の更新に成功"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<ToggleCredentialResponseDto> update(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id,
            @RequestBody ToggleCredentialUpdateRequestDto request) {
        
        try {
            ToggleCredentialResponseDto response = toggleCredentialUseCase.update(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Toggle認証情報削除", description = "指定されたIDのToggle認証情報を削除します")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "認証情報の削除に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        try {
            toggleCredentialUseCase.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/exists")
    @Operation(summary = "Toggle認証情報存在確認", description = "指定されたユーザーにToggle認証情報が存在するかチェックします")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "存在確認の結果を返却"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<Boolean> exists(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        boolean exists = toggleCredentialUseCase.existsByUserId(userId);
        return ResponseEntity.ok(exists);
    }
}