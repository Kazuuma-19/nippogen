package com.example.backend.application.dto.reports;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Markdownエクスポート用DTO
 * 日報をMarkdown形式で出力するための情報を転送するDTO
 */
@Getter
@Builder
public class MarkdownExportDto {
    
    private final String content;
    private final String fileName;
    private final LocalDate reportDate;
    private final String userName;
    
    /**
     * デフォルトのファイル名を生成
     * 
     * @return 生成されたファイル名
     */
    public String getDefaultFileName() {
        if (fileName != null && !fileName.trim().isEmpty()) {
            return fileName;
        }
        
        if (reportDate != null) {
            String dateStr = reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (userName != null && !userName.trim().isEmpty()) {
                return String.format("%s_%s_daily_report.md", dateStr, userName);
            }
            return String.format("%s_daily_report.md", dateStr);
        }
        
        return "daily_report.md";
    }
    
    /**
     * Markdownヘッダーを生成
     * 
     * @return Markdownヘッダー文字列
     */
    public String generateMarkdownHeader() {
        StringBuilder header = new StringBuilder();
        
        if (reportDate != null) {
            String dateStr = reportDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
            header.append("# 日報 - ").append(dateStr).append("\n\n");
        } else {
            header.append("# 日報\n\n");
        }
        
        if (userName != null && !userName.trim().isEmpty()) {
            header.append("**作成者:** ").append(userName).append("\n");
        }
        
        if (reportDate != null) {
            header.append("**日付:** ").append(reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        }
        
        header.append("\n---\n\n");
        
        return header.toString();
    }
    
    /**
     * 完全なMarkdownコンテンツを生成
     * 
     * @return ヘッダー付きのMarkdownコンテンツ
     */
    public String getFullMarkdownContent() {
        StringBuilder fullContent = new StringBuilder();
        fullContent.append(generateMarkdownHeader());
        
        if (content != null && !content.trim().isEmpty()) {
            fullContent.append(content);
        } else {
            fullContent.append("*コンテンツがありません*");
        }
        
        return fullContent.toString();
    }
    
    /**
     * コンテンツが存在するかチェック
     * 
     * @return コンテンツが存在する場合true
     */
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }
}
