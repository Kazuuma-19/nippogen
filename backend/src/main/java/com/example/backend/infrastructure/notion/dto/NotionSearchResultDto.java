package com.example.backend.infrastructure.notion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotionSearchResultDto {
    
    @JsonProperty("object")
    private String object; // "list"
    
    @JsonProperty("results")
    private List<NotionPageDto> results;
    
    @JsonProperty("next_cursor")
    private String nextCursor;
    
    @JsonProperty("has_more")
    private Boolean hasMore;
    
    @JsonProperty("type")
    private String type; // "page_or_database"
    
    @JsonProperty("page_or_database")
    private Object pageOrDatabase; // これはページかデータベースのオブジェクト
}