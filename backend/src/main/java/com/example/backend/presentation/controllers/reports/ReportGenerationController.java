package com.example.backend.presentation.controllers.reports;

import com.example.backend.presentation.dto.reports.ReportGenerationRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationResponseDto;
import com.example.backend.presentation.dto.reports.ReportRegenerationRequestDto;
import com.example.backend.application.usecases.reports.ReportGenerationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * AI日報生成REST APIコントローラー
 * OpenAI GPT-5-miniを使用した日報生成・再生成機能を提供
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "AI日報生成", description = "GPT-5-miniを使用したAI駆動の日報生成")
@RequiredArgsConstructor
public class ReportGenerationController {
    
    private final ReportGenerationUseCase reportGenerationUseCase;
    
    @PostMapping("/generate")
    @Operation(
        summary = "日報生成", 
        description = "GitHub、Toggl、NotionデータをもとにAIで新しい日報を生成"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "日報の生成に成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "リクエストデータが不正または指定日の日報が既に存在",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "サーバー内部エラーまたはAI生成失敗",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        )
    })
    public ResponseEntity<ReportGenerationResponseDto> generateReport(
            @Parameter(description = "日報生成リクエスト", required = true)
            @RequestBody ReportGenerationRequestDto request
    ) {
        ReportGenerationResponseDto response = reportGenerationUseCase.generateReport(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(201).body(response);
        } else {
            // エラーメッセージに応じて適切なHTTPステータスを返す
            if (response.getErrorMessage() != null && 
                response.getErrorMessage().contains("既に存在")) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/{id}/regenerate")
    @Operation(
        summary = "日報再生成", 
        description = "ユーザーフィードバックと追加情報で既存の日報を再生成"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "日報の再生成に成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "リクエストデータが不正",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "日報が見つからない",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "サーバー内部エラーまたはAI再生成失敗",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        )
    })
    public ResponseEntity<ReportGenerationResponseDto> regenerateReport(
            @Parameter(description = "日報ID", required = true)
            @PathVariable UUID id,
            
            @Parameter(description = "日報再生成リクエスト", required = true)
            @RequestBody ReportRegenerationRequestDto request
    ) {
        // リクエストにreportIdを設定（パスパラメータから）
        ReportRegenerationRequestDto requestWithId = ReportRegenerationRequestDto.builder()
            .reportId(id)
            .userFeedback(request.getUserFeedback())
            .additionalNotes(request.getAdditionalNotes())
            .build();
        
        ReportGenerationResponseDto response = reportGenerationUseCase.regenerateReport(requestWithId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // エラーメッセージに応じて適切なHTTPステータスを返す
            if (response.getErrorMessage() != null && 
                response.getErrorMessage().contains("見つかりません")) {
                return ResponseEntity.notFound().build();
            }
            if (response.getErrorMessage() != null && 
                response.getErrorMessage().contains("必須項目")) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
