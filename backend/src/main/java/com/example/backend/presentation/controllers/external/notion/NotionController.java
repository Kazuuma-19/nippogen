package com.example.backend.presentation.controllers.external.notion;

import com.example.backend.application.dto.external.notion.NotionPageDto;
import com.example.backend.application.dto.external.notion.NotionTableRowDto;
import com.example.backend.application.usecases.external.notion.NotionUseCase;
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
@RequestMapping("/api/external/notion")
@Tag(name = "Notion Integration", description = "Notion API integration endpoints for retrieving page and table content")
@RequiredArgsConstructor
public class NotionController {
    
    private final NotionUseCase notionUseCase;
    
    @GetMapping("/test")
    @Operation(
        summary = "Test Notion connection", 
        description = "Test Notion API connection to verify access permissions"
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
            description = "Internal server error or Notion API connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<Boolean> testConnection() {
        boolean isConnected = notionUseCase.testConnection();
        return ResponseEntity.ok(isConnected);
    }
    
    @GetMapping("/page")
    @Operation(
        summary = "Get page content", 
        description = "Retrieve content from the pre-configured Notion page"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Page content retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NotionPageDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Page not found or access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or Notion API connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<NotionPageDto> getPageContent() {
        NotionPageDto page = notionUseCase.getPageContent();
        return ResponseEntity.ok(page);
    }
    
    @GetMapping("/table")
    @Operation(
        summary = "Get table content", 
        description = "Retrieve all rows from the pre-configured Notion table"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Table content retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NotionTableRowDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Table not found or access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or Notion API connection failure",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    public ResponseEntity<List<NotionTableRowDto>> getTableContent() {
        List<NotionTableRowDto> rows = notionUseCase.getTableContent();
        return ResponseEntity.ok(rows);
    }
}
