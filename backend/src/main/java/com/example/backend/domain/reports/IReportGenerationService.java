package com.example.backend.domain.reports;

import java.time.LocalDate;
import java.util.UUID;

/**
 * AI日報生成サービスのドメインインターフェース
 * 日報生成・再生成の抽象化を提供
 */
public interface IReportGenerationService {
    
    /**
     * 新規日報を生成する
     * 
     * @param userId ユーザーID
     * @param reportDate 日報対象日
     * @param githubData GitHubからの活動データ
     * @param togglData Toggleからの時間記録データ
     * @param notionData Notionからのコンテンツデータ
     * @param additionalNotes ユーザーからの追加情報
     * @return 生成された日報コンテンツ
     */
    String generateReport(
        UUID userId, 
        LocalDate reportDate,
        String githubData,
        String togglData, 
        String notionData,
        String additionalNotes
    );
    
    /**
     * 既存日報を再生成する
     * 
     * @param userId ユーザーID
     * @param reportDate 日報対象日
     * @param githubData GitHubからの活動データ
     * @param togglData Toggleからの時間記録データ
     * @param notionData Notionからのコンテンツデータ
     * @param previousContent 前回生成されたコンテンツ
     * @param userFeedback ユーザーからのフィードバック
     * @param additionalNotes ユーザーからの追加情報
     * @return 再生成された日報コンテンツ
     */
    String regenerateReport(
        UUID userId,
        LocalDate reportDate,
        String githubData,
        String togglData,
        String notionData,
        String previousContent,
        String userFeedback,
        String additionalNotes
    );
}
