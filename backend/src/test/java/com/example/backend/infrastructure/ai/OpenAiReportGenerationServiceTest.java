package com.example.backend.infrastructure.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OpenAiReportGenerationService テスト")
class OpenAiReportGenerationServiceTest {
    
    @Mock
    private ChatModel chatModel;
    
    private OpenAiReportGenerationService service;
    
    @BeforeEach
    void setUp() {
        service = new OpenAiReportGenerationService(chatModel);
    }
    
    @Test
    @DisplayName("正常な日報生成テスト")
    void generateReport_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        String githubData = "{\"commits\": 5}";
        String togglData = "{\"hours\": 8}";
        String notionData = "{\"notes\": \"開発メモ\"}";
        String additionalNotes = "追加情報";
        
        String expectedContent = """
            # 日報 - 2024年01月15日
            
            ## 今日の成果
            - コミット5件を実装
            
            ## 技術的学び
            - 新しいAPIの実装方法を習得
            """;
        
        // Mock ChatResponse
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(expectedContent);
        when(generation.getOutput()).thenReturn(assistantMessage);
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        when(chatResponse.getResult()).thenReturn(generation);
        
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        
        // When
        String result = service.generateReport(
            userId, reportDate, githubData, togglData, notionData, additionalNotes
        );
        
        // Then
        assertThat(result).isEqualTo(expectedContent);
        
        // Verify
        verify(chatModel, times(1)).call(any(Prompt.class));
    }
    
    @Test
    @DisplayName("空データでの日報生成テスト")
    void generateReport_EmptyData() {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        String githubData = "{}";
        String togglData = "{}";
        String notionData = "{}";
        String additionalNotes = null;
        
        String expectedContent = """
            # 日報 - 2024年01月15日
            
            ## 今日の成果
            データが不足しているため、具体的な成果を記載できません。
            """;
        
        // Mock ChatResponse
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(expectedContent);
        when(generation.getOutput()).thenReturn(assistantMessage);
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        when(chatResponse.getResult()).thenReturn(generation);
        
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        
        // When
        String result = service.generateReport(
            userId, reportDate, githubData, togglData, notionData, additionalNotes
        );
        
        // Then
        assertThat(result).isEqualTo(expectedContent);
        
        // Verify
        verify(chatModel, times(1)).call(any(Prompt.class));
    }
    
    @Test
    @DisplayName("正常な日報再生成テスト")
    void regenerateReport_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        String githubData = "{\"commits\": 5}";
        String togglData = "{\"hours\": 8}";
        String notionData = "{\"notes\": \"開発メモ\"}";
        String previousContent = "前回の日報内容";
        String userFeedback = "もう少し詳細に書いてほしい";
        String additionalNotes = "追加情報";
        
        String expectedContent = """
            # 日報 - 2024年01月15日 (改訂版)
            
            ## 今日の成果
            - コミット5件を実装（詳細追記）
            """;
        
        // Mock ChatResponse
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(expectedContent);
        when(generation.getOutput()).thenReturn(assistantMessage);
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        when(chatResponse.getResult()).thenReturn(generation);
        
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
        
        // When
        String result = service.regenerateReport(
            userId, reportDate, githubData, togglData, notionData,
            previousContent, userFeedback, additionalNotes
        );
        
        // Then
        assertThat(result).isEqualTo(expectedContent);
        
        // Verify
        verify(chatModel, times(1)).call(any(Prompt.class));
    }
    
    @Test
    @DisplayName("ChatClient例外処理テスト")
    void generateReport_ChatClientException() {
        // Given
        UUID userId = UUID.randomUUID();
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        
        when(chatModel.call(any(Prompt.class)))
            .thenThrow(new RuntimeException("OpenAI API error"));
        
        // When & Then
        assertThatThrownBy(() -> service.generateReport(
            userId, reportDate, "{}", "{}", "{}", null
        ))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("AI日報生成に失敗しました")
            .hasCauseInstanceOf(RuntimeException.class);
        
        // Verify
        verify(chatModel, times(1)).call(any(Prompt.class));
    }
}