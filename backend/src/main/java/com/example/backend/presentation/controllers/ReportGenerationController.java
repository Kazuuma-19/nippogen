package com.example.backend.presentation.controllers;

import com.example.backend.application.dto.ReportGenerationRequestDto;
import com.example.backend.application.dto.ReportGenerationResponseDto;
import com.example.backend.application.dto.ReportRegenerationRequestDto;
import com.example.backend.application.usecases.ReportGenerationUseCase;
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
@Tag(name = "AI Report Generation", description = "AI-powered daily report generation using GPT-5-mini")
@RequiredArgsConstructor
public class ReportGenerationController {
    
    private final ReportGenerationUseCase reportGenerationUseCase;
    
    @PostMapping("/generate")
    @Operation(
        summary = "Generate daily report", 
        description = "Generate a new daily report using AI based on GitHub, Toggl, and Notion data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Report generated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request data or report already exists for the date",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or AI generation failure",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        )
    })
    public ResponseEntity<ReportGenerationResponseDto> generateReport(
            @Parameter(description = "Report generation request", required = true)
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
        summary = "Regenerate daily report", 
        description = "Regenerate an existing daily report with user feedback and additional information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Report regenerated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Report not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or AI regeneration failure",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReportGenerationResponseDto.class)
            )
        )
    })
    public ResponseEntity<ReportGenerationResponseDto> regenerateReport(
            @Parameter(description = "Report ID", required = true)
            @PathVariable UUID id,
            
            @Parameter(description = "Report regeneration request", required = true)
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