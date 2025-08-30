package com.example.backend.presentation.controllers.credentials.notion;

import com.example.backend.application.dto.credentials.notion.NotionCredentialCreateRequestDto;
import com.example.backend.application.dto.credentials.notion.NotionCredentialResponseDto;
import com.example.backend.application.dto.credentials.notion.NotionCredentialUpdateRequestDto;
import com.example.backend.application.services.credentials.notion.NotionCredentialService;

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
@RequestMapping("/api/credentials/notion")
@RequiredArgsConstructor
@Tag(name = "Notion認証情報", description = "Notion API認証情報の管理")
public class NotionCredentialController {
    
    private final NotionCredentialService notionCredentialService;
    
    @PostMapping
    @Operation(summary = "Notion認証情報作成", description = "新しいNotion認証情報を作成します")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "認証情報の作成に成功"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<NotionCredentialResponseDto> create(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody NotionCredentialCreateRequestDto request) {
        
        NotionCredentialResponseDto response = notionCredentialService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Notion認証情報取得", description = "指定されたIDのNotion認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<NotionCredentialResponseDto> findById(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        return notionCredentialService.findById(id)
                .map(credential -> ResponseEntity.ok(credential))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "ユーザーのNotion認証情報取得", description = "指定されたユーザーのアクティブなNotion認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<NotionCredentialResponseDto> findByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        return notionCredentialService.findByUserId(userId)
                .map(credential -> ResponseEntity.ok(credential))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/all")
    @Operation(summary = "ユーザーの全Notion認証情報取得", description = "指定されたユーザーの全Notion認証情報を取得します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の取得に成功"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<List<NotionCredentialResponseDto>> findAllByUserId(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        List<NotionCredentialResponseDto> credentials = notionCredentialService.findAllByUserId(userId);
        return ResponseEntity.ok(credentials);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Notion認証情報更新", description = "指定されたIDのNotion認証情報を更新します")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "認証情報の更新に成功"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<NotionCredentialResponseDto> update(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id,
            @RequestBody NotionCredentialUpdateRequestDto request) {
        
        try {
            NotionCredentialResponseDto response = notionCredentialService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Notion認証情報削除", description = "指定されたIDのNotion認証情報を削除します")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "認証情報の削除に成功"),
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "認証情報ID", required = true) @PathVariable UUID id) {
        
        try {
            notionCredentialService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/exists")
    @Operation(summary = "Notion認証情報存在確認", description = "指定されたユーザーにNotion認証情報が存在するかチェックします")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "存在確認の結果を返却"),
        @ApiResponse(responseCode = "500", description = "サーバーエラー")
    })
    public ResponseEntity<Boolean> exists(
            @Parameter(description = "ユーザーID", required = true) @RequestHeader("X-User-Id") UUID userId) {
        
        boolean exists = notionCredentialService.existsByUserId(userId);
        return ResponseEntity.ok(exists);
    }
}