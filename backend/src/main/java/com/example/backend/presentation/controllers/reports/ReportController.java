package com.example.backend.presentation.controllers.reports;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.application.usecases.reports.ReportUseCase;
import com.example.backend.application.usecases.reports.ReportGenerationUseCase;
import com.example.backend.common.exceptions.ReportNotFoundException;
import com.example.backend.common.util.CommonApiResponses;
import com.example.backend.presentation.dto.reports.DailyReportListResponseDto;
import com.example.backend.presentation.dto.reports.DailyReportUpdateRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationRequestDto;
import com.example.backend.presentation.dto.reports.ReportGenerationResponseDto;
import com.example.backend.presentation.dto.reports.ReportRegenerationRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * 日報管理REST APIコントローラー
 * 日報のCRUD操作およびAI生成機能を提供
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "日報管理", description = "日報のCRUD操作とAI生成機能")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportUseCase reportUseCase;
    private final ReportGenerationUseCase reportGenerationUseCase;
    
    @GetMapping
    @Operation(
        summary = "日報一覧取得", 
        description = "日付範囲とステータスによる任意フィルタリングで日報一覧を取得"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "日報の取得に成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DailyReportListResponseDto.class)
            )
        )
    })
    @CommonApiResponses.StandardErrorResponses
    public ResponseEntity<DailyReportListResponseDto> getReports(
            @Parameter(description = "ユーザーID", required = true)
            @RequestParam UUID userId,
            
            @Parameter(description = "フィルタリング開始日 (YYYY-MM-DD形式)", example = "2024-01-01")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "フィルタリング終了日 (YYYY-MM-DD形式)", example = "2024-12-31")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        DailyReportListResponseDto response = reportUseCase.getReportsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{date}")
    @Operation(
        summary = "日付指定日報取得", 
        description = "指定された日付の日報を取得"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "日報の取得に成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DailyReportDto.class)
            )
        )
    })
    @CommonApiResponses.WithNotFound
    public ResponseEntity<DailyReportDto> getReportByDate(
            @Parameter(description = "日報日付 (YYYY-MM-DD形式)", example = "2024-01-15", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            
            @Parameter(description = "ユーザーID", required = true)
            @RequestParam UUID userId
    ) {
        Optional<DailyReportDto> report = reportUseCase.getReportByDate(userId, date);
        return report.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    
    @PutMapping("/{id}")
    @Operation(
        summary = "日報更新", 
        description = "既存の日報内容を更新"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "日報の更新に成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DailyReportDto.class)
            )
        )
    })
    @CommonApiResponses.WithNotFound
    public ResponseEntity<DailyReportDto> updateReport(
            @Parameter(description = "日報ID", required = true)
            @PathVariable UUID id,
            
            @Parameter(description = "日報更新リクエスト", required = true)
            @RequestBody DailyReportUpdateRequestDto request
    ) {
        DailyReportDto updatedReport = reportUseCase.updateReport(id, request);
        return ResponseEntity.ok(updatedReport);
    }
    
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "日報削除", 
        description = "日報を削除"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "日報の削除に成功"
        )
    })
    @CommonApiResponses.WithNotFound
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "日報ID", required = true)
            @PathVariable UUID id
    ) {
        boolean deleted = reportUseCase.deleteReport(id);
        if (!deleted) {
            throw new ReportNotFoundException("Report not found for deletion: " + id);
        }
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/generate")
    @Operation(
        summary = "AI日報生成", 
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
        )
    })
    @CommonApiResponses.StandardErrorResponses
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
        summary = "AI日報再生成", 
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
        )
    })
    @CommonApiResponses.WithNotFound
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
