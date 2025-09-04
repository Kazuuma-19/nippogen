package com.example.backend.infrastructure.notion;

import com.example.backend.domain.credentials.notion.NotionCredential;
import com.example.backend.infrastructure.notion.dto.NotionPageDto;
import com.example.backend.infrastructure.notion.dto.NotionDatabaseDto;
import com.example.backend.infrastructure.notion.dto.NotionSearchResultDto;
import com.example.backend.infrastructure.notion.dto.NotionUserDto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class NotionApiService {
    
    private final WebClient webClient;
    
    public NotionApiService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.notion.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Notion-Version", "2022-06-28")
            .defaultHeader(HttpHeaders.USER_AGENT, "Nippogen-Backend/1.0")
            .build();
    }
    
    /**
     * Notion API接続テスト
     * 
     * @param credential Notion認証情報
     * @return 接続成功時true
     */
    public boolean testConnection(NotionCredential credential) {
        try {
            String authHeader = "Bearer " + credential.getApiKey();
            
            NotionUserDto user = webClient
                .get()
                .uri("/users/me")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(NotionUserDto.class)
                .block();
                
            return user != null;
            
        } catch (WebClientResponseException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 指定したデータベースIDからページリストを取得
     * 
     * @param credential Notion認証情報
     * @param databaseId データベースID
     * @param date 対象日（フィルタリング用、nullの場合は全件取得）
     * @return ページリスト
     */
    public List<NotionPageDto> getPagesByDatabase(NotionCredential credential, String databaseId, LocalDate date) {
        try {
            String authHeader = "Bearer " + credential.getApiKey();
            
            // フィルタ条件を構築
            Map<String, Object> filter = null;
            if (date != null) {
                String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
                filter = Map.of(
                    "property", "Created time", // Notionのデフォルトプロパティ
                    "date", Map.of(
                        "on_or_after", dateString
                    )
                );
            }
            
            Map<String, Object> requestBody = Map.of(
                "page_size", 100
            );
            
            if (filter != null) {
                requestBody = Map.of(
                    "page_size", 100,
                    "filter", filter
                );
            }
            
            NotionSearchResultDto result = webClient
                .post()
                .uri("/databases/{database_id}/query", databaseId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(NotionSearchResultDto.class)
                .block();
                
            if (result != null && result.getResults() != null) {
                return result.getResults();
            }
                
            return List.of();
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Notion認証に失敗しました。Integration Tokenを確認してください。", e);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new RuntimeException("Notionデータベースへのアクセス権限がありません。", e);
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("指定されたNotionデータベースが見つかりません。", e);
            }
            
            throw new RuntimeException("Notion APIの呼び出しに失敗しました: " + e.getMessage(), e);
            
        } catch (Exception e) {
            throw new RuntimeException("Notionデータの取得中にエラーが発生しました。", e);
        }
    }
    
    /**
     * キーワード検索でページを取得
     * 
     * @param credential Notion認証情報
     * @param query 検索クエリ
     * @return 検索結果ページリスト
     */
    public List<NotionPageDto> searchPages(NotionCredential credential, String query) {
        try {
            String authHeader = "Bearer " + credential.getApiKey();
            
            Map<String, Object> requestBody = Map.of(
                "query", query,
                "page_size", 100,
                "filter", Map.of(
                    "value", "page",
                    "property", "object"
                )
            );
            
            NotionSearchResultDto result = webClient
                .post()
                .uri("/search")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(NotionSearchResultDto.class)
                .block();
                
            if (result != null && result.getResults() != null) {
                return result.getResults();
            }
                
            return List.of();
            
        } catch (Exception e) {
            throw new RuntimeException("Notionの検索中にエラーが発生しました。", e);
        }
    }
    
    /**
     * 指定したページIDの詳細情報を取得
     * 
     * @param credential Notion認証情報
     * @param pageId ページID
     * @return ページ詳細
     */
    public NotionPageDto getPageById(NotionCredential credential, String pageId) {
        try {
            String authHeader = "Bearer " + credential.getApiKey();
            
            NotionPageDto page = webClient
                .get()
                .uri("/pages/{page_id}", pageId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(NotionPageDto.class)
                .block();
                
            return page;
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("指定されたNotionページが見つかりません。", e);
            }
            
            throw new RuntimeException("Notionページの取得に失敗しました: " + e.getMessage(), e);
            
        } catch (Exception e) {
            throw new RuntimeException("Notionページの取得中にエラーが発生しました。", e);
        }
    }
    
    /**
     * Notion統計データクラス
     */
    public record NotionStats(
        int totalPages,
        int totalDatabases,
        List<NotionPageDto> recentPages
    ) {}
}