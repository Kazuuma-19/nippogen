package com.example.backend.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GitHubSearchResultDto<T>(
        @JsonProperty("total_count") int totalCount,
        @JsonProperty("incomplete_results") boolean incompleteResults,
        List<T> items
) {
}