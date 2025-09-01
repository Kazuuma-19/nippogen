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
@Tag(name = "Daily Report Management", description = "Daily report CRUD operations and export functionality")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportUseCase reportUseCase;
    
    @GetMapping
    @Operation(
        summary = "Get daily reports list", 
        description = "Retrieve daily reports with optional filtering by date range and status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reports retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DailyReportListResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<DailyReportListResponseDto> getReports(
            @Parameter(description = "User ID", required = true)
            @RequestParam UUID userId,
            
            @Parameter(description = "Start date for filtering (YYYY-MM-DD format)", example = "2024-01-01")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date for filtering (YYYY-MM-DD format)", example = "2024-12-31")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        DailyReportListResponseDto response = reportUseCase.getReportsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{date}")
    @Operation(
        summary = "Get daily report by date", 
        description = "Retrieve a specific daily report for the given date"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Report retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DailyReportDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Report not found for the specified date",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid date format",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<DailyReportDto> getReportByDate(
            @Parameter(description = "Report date in YYYY-MM-DD format", example = "2024-01-15", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            
            @Parameter(description = "User ID", required = true)
            @RequestParam UUID userId
    ) {
        Optional<DailyReportDto> report = reportUseCase.getReportByDate(userId, date);
        return report.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update daily report", 
        description = "Update an existing daily report content"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Report updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DailyReportDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request data or report not editable",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Report not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<DailyReportDto> updateReport(
            @Parameter(description = "Report ID", required = true)
            @PathVariable UUID id,
            
            @Parameter(description = "Report update request", required = true)
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
        summary = "Delete daily report", 
        description = "Delete a daily report"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Report deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Report not found"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "Report ID", required = true)
            @PathVariable UUID id
    ) {
        boolean deleted = reportUseCase.deleteReport(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
