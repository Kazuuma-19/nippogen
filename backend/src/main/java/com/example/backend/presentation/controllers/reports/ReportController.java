package com.example.backend.presentation.controllers.reports;

import com.example.backend.application.dto.reports.DailyReportDto;
import com.example.backend.application.usecases.reports.ReportUseCase;
import com.example.backend.presentation.dto.reports.DailyReportListResponseDto;
import com.example.backend.presentation.dto.reports.DailyReportUpdateRequestDto;

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
 * 日報のCRUD操作およびエクスポート機能を提供
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "日報管理", description = "日報の作成・読取・更新・削除操作")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportUseCase reportUseCase;
    
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
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "リクエストパラメータが不正",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "サーバー内部エラー",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
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
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "指定された日付の日報が見つからない",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "日付形式が不正",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "サーバー内部エラー",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
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
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "リクエストデータが不正または編集不可",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "日報が見つからない",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "サーバー内部エラー",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<DailyReportDto> updateReport(
            @Parameter(description = "日報ID", required = true)
            @PathVariable UUID id,
            
            @Parameter(description = "日報更新リクエスト", required = true)
            @RequestBody DailyReportUpdateRequestDto request
    ) {
        try {
            DailyReportDto updatedReport = reportUseCase.updateReport(id, request);
            return ResponseEntity.ok(updatedReport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
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
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "日報が見つからない"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "サーバー内部エラー",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "日報ID", required = true)
            @PathVariable UUID id
    ) {
        boolean deleted = reportUseCase.deleteReport(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
