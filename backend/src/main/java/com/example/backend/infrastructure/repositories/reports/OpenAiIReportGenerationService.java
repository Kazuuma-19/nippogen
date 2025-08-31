package com.example.backend.infrastructure.repositories.reports;

import com.example.backend.domain.reports.IReportGenerationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * OpenAI GPT-5-miniを使用した日報生成サービス実装
 * Spring AI ChatClientを使用してAI日報生成を実装
 */
@Service
public class OpenAiIReportGenerationService implements IReportGenerationService {
    
    private final ChatClient chatClient;
    
    public OpenAiIReportGenerationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    private static final String SYSTEM_PROMPT = """
        あなたは優秀なソフトウェアエンジニアの日報作成アシスタントです。
        与えられた情報を基に、技術的な振り返りを含む高品質な日報をMarkdown形式で生成してください。

        ## 日報の構成
        1. **今日の成果** - 実装した機能やタスクの完了状況
        2. **技術的学び** - 使用した技術、ツール、解決した問題
        3. **課題と改善点** - 遭遇した問題、今後の改善案
        4. **明日の予定** - 次の作業予定、継続タスク

        ## 出力要件
        - Markdown形式で出力
        - 簡潔で読みやすい文章
        - 技術的な詳細を含める
        - データが不足している場合は、その旨を記載
        """;
    
    @Override
    public String generateReport(
        UUID userId, 
        LocalDate reportDate,
        String githubData,
        String togglData, 
        String notionData,
        String additionalNotes
    ) {
        try {
            
            String userPrompt = buildGenerationPrompt(
                reportDate, githubData, togglData, notionData, additionalNotes
            );
            
            String generatedContent = callOpenAI(userPrompt);
            
            return generatedContent;
            
        } catch (Exception e) {
            throw new RuntimeException("AI日報生成に失敗しました", e);
        }
    }
    
    @Override
    public String regenerateReport(
        UUID userId,
        LocalDate reportDate,
        String githubData,
        String togglData,
        String notionData,
        String previousContent,
        String userFeedback,
        String additionalNotes
    ) {
        try {
            
            String userPrompt = buildRegenerationPrompt(
                reportDate, githubData, togglData, notionData, 
                previousContent, userFeedback, additionalNotes
            );
            
            String regeneratedContent = callOpenAI(userPrompt);
            
            return regeneratedContent;
            
        } catch (Exception e) {
            throw new RuntimeException("AI日報再生成に失敗しました", e);
        }
    }
    
    /**
     * 新規日報生成用プロンプトを構築
     */
    private String buildGenerationPrompt(
        LocalDate reportDate,
        String githubData,
        String togglData,
        String notionData,
        String additionalNotes
    ) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("## 日報作成対象日\n");
        prompt.append(reportDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).append("\n\n");
        
        prompt.append("## 利用可能なデータ\n\n");
        
        // GitHubデータ
        prompt.append("### GitHubアクティビティ\n");
        if (githubData != null && !githubData.equals("{}")) {
            prompt.append("```json\n").append(githubData).append("\n```\n\n");
        } else {
            prompt.append("データなし\n\n");
        }
        
        // Togglデータ
        prompt.append("### 時間記録（Toggl）\n");
        if (togglData != null && !togglData.equals("{}")) {
            prompt.append("```json\n").append(togglData).append("\n```\n\n");
        } else {
            prompt.append("データなし\n\n");
        }
        
        // Notionデータ
        prompt.append("### ドキュメント・メモ（Notion）\n");
        if (notionData != null && !notionData.equals("{}")) {
            prompt.append("```json\n").append(notionData).append("\n```\n\n");
        } else {
            prompt.append("データなし\n\n");
        }
        
        // 追加情報
        if (additionalNotes != null && !additionalNotes.trim().isEmpty()) {
            prompt.append("### ユーザー追加情報\n");
            prompt.append(additionalNotes).append("\n\n");
        }
        
        prompt.append("上記の情報を基に、技術的な振り返りを含む日報をMarkdown形式で作成してください。");
        
        return prompt.toString();
    }
    
    /**
     * 日報再生成用プロンプトを構築
     */
    private String buildRegenerationPrompt(
        LocalDate reportDate,
        String githubData,
        String togglData,
        String notionData,
        String previousContent,
        String userFeedback,
        String additionalNotes
    ) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("## 日報再生成対象日\n");
        prompt.append(reportDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).append("\n\n");
        
        prompt.append("## 前回生成された日報\n");
        prompt.append("```markdown\n").append(previousContent).append("\n```\n\n");
        
        if (userFeedback != null && !userFeedback.trim().isEmpty()) {
            prompt.append("## ユーザーフィードバック\n");
            prompt.append(userFeedback).append("\n\n");
        }
        
        // データセクションは新規生成と同じ
        prompt.append("## 利用可能なデータ\n\n");
        
        // GitHubデータ
        prompt.append("### GitHubアクティビティ\n");
        if (githubData != null && !githubData.equals("{}")) {
            prompt.append("```json\n").append(githubData).append("\n```\n\n");
        } else {
            prompt.append("データなし\n\n");
        }
        
        // Togglデータ
        prompt.append("### 時間記録（Toggl）\n");
        if (togglData != null && !togglData.equals("{}")) {
            prompt.append("```json\n").append(togglData).append("\n```\n\n");
        } else {
            prompt.append("データなし\n\n");
        }
        
        // Notionデータ
        prompt.append("### ドキュメント・メモ（Notion）\n");
        if (notionData != null && !notionData.equals("{}")) {
            prompt.append("```json\n").append(notionData).append("\n```\n\n");
        } else {
            prompt.append("データなし\n\n");
        }
        
        // 追加情報
        if (additionalNotes != null && !additionalNotes.trim().isEmpty()) {
            prompt.append("### 追加情報\n");
            prompt.append(additionalNotes).append("\n\n");
        }
        
        prompt.append("前回の日報とフィードバックを参考に、改善された日報をMarkdown形式で再生成してください。");
        
        return prompt.toString();
    }
    
    /**
     * OpenAI APIを呼び出して日報を生成
     */
    private String callOpenAI(String userPrompt) {
        return chatClient.prompt()
            .system(SYSTEM_PROMPT)
            .user(userPrompt)
            .call()
            .content();
    }
}
