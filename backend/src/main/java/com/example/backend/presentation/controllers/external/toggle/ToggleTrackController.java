package com.example.backend.presentation.controllers.external.toggle;

import com.example.backend.application.dto.external.toggle.TimeEntryDto;
import com.example.backend.application.usecases.external.toggle.ToggleTrackUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external/toggl")
@Tag(name = "ToggleTrack Integration", description = "ToggleTrack API integration endpoints for retrieving time tracking data")
@RequiredArgsConstructor
public class ToggleTrackController {
    
    private final ToggleTrackUseCase toggleTrackUseCase;
    
    @GetMapping("/test")
    @Operation(
        summary = "Test ToggleTrack connection", 
        description = "Test ToggleTrack API connection to verify access permissions"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Connection test completed successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(type = "boolean", example = "true")
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or ToggleTrack API connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<Boolean> testConnection() {
        boolean isConnected = toggleTrackUseCase.testConnection();
        return ResponseEntity.ok(isConnected);
    }
    
    @GetMapping("/today")
    @Operation(
        summary = "Get today's time entries", 
        description = "Retrieve all time entries for today from ToggleTrack. Backend automatically uses current date."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Today's time entries retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TimeEntryDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - invalid ToggleTrack credentials",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or ToggleTrack API connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<List<TimeEntryDto>> getTodayTimeEntries() {
        List<TimeEntryDto> timeEntries = toggleTrackUseCase.getTodayTimeEntries();
        return ResponseEntity.ok(timeEntries);
    }
}
