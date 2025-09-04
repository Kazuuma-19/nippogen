package com.example.backend.infrastructure.toggl;

import com.example.backend.domain.credentials.toggl.TogglCredential;
import com.example.backend.infrastructure.toggl.dto.TogglTimeEntryDto;
import com.example.backend.infrastructure.toggl.dto.TogglUserDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class TogglApiService {
    
    private final WebClient webClient;
    
    public TogglApiService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.track.toggl.com/api/v9")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .defaultHeader(HttpHeaders.USER_AGENT, "Nippogen-Backend/1.0")
            .build();
    }
    
    /**
     * Toggl Track接続テスト
     * 
     * @param credential Toggl認証情報
     * @return 接続成功時true
     */
    public boolean testConnection(TogglCredential credential) {
        try {
            String authHeader = createAuthorizationHeader(credential.getApiToken());
            
            TogglUserDto user = webClient
                .get()
                .uri("/me")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(TogglUserDto.class)
                .block();
                
            log.info("Toggl接続テスト成功: user={}", user != null ? user.getEmail() : "unknown");
            return user != null;
            
        } catch (WebClientResponseException e) {
            log.error("Toggl接続テスト失敗: status={}, message={}", 
                e.getStatusCode(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Toggl接続テスト中に予期しないエラー", e);
            return false;
        }
    }
    
    /**
     * 指定日の時間記録を取得
     * 
     * @param credential Toggl認証情報
     * @param date 対象日
     * @return 時間記録リスト
     */
    public List<TogglTimeEntryDto> getTimeEntriesByDate(TogglCredential credential, LocalDate date) {
        try {
            String authHeader = createAuthorizationHeader(credential.getApiToken());
            String startDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String endDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            List<TogglTimeEntryDto> timeEntries = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/me/time_entries")
                    .queryParam("start_date", startDate)
                    .queryParam("end_date", endDate)
                    .build())
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToFlux(TogglTimeEntryDto.class)
                .collectList()
                .block();
                
            log.info("Toggl APIから{}件の時間記録を取得: date={}", 
                timeEntries != null ? timeEntries.size() : 0, date);
                
            return timeEntries != null ? timeEntries : List.of();
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error("Toggl API認証エラー: APIトークンが無効または期限切れの可能性があります");
                throw new RuntimeException("Toggl認証に失敗しました。APIトークンを確認してください。", e);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.error("Toggl API制限エラー: レート制限または権限不足");
                throw new RuntimeException("Toggl APIへのアクセスが制限されています。", e);
            }
            
            log.error("Toggl API呼び出しエラー: status={}, message={}", 
                e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Toggl APIの呼び出しに失敗しました: " + e.getMessage(), e);
            
        } catch (Exception e) {
            log.error("Toggl API呼び出し中に予期しないエラーが発生", e);
            throw new RuntimeException("Togglデータの取得中にエラーが発生しました。", e);
        }
    }
    
    /**
     * 指定期間の時間記録統計を取得
     * 
     * @param credential Toggl認証情報
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 時間記録統計
     */
    public TogglTimeStats getTimeStats(TogglCredential credential, LocalDate startDate, LocalDate endDate) {
        try {
            String authHeader = createAuthorizationHeader(credential.getApiToken());
            String start = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String end = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            List<TogglTimeEntryDto> timeEntries = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/me/time_entries")
                    .queryParam("start_date", start)
                    .queryParam("end_date", end)
                    .build())
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToFlux(TogglTimeEntryDto.class)
                .collectList()
                .block();
                
            if (timeEntries == null) {
                return new TogglTimeStats(0, 0, 0, List.of());
            }
            
            int totalEntries = timeEntries.size();
            long totalDurationSeconds = timeEntries.stream()
                .mapToLong(entry -> entry.getDuration() != null ? entry.getDuration() : 0L)
                .sum();
            double totalHours = totalDurationSeconds / 3600.0;
            
            return new TogglTimeStats(totalEntries, totalDurationSeconds, totalHours, timeEntries);
            
        } catch (Exception e) {
            log.error("Toggl統計データ取得エラー", e);
            throw new RuntimeException("Togglの統計データ取得に失敗しました。", e);
        }
    }
    
    /**
     * Basic認証ヘッダーを作成
     * TogglはAPIトークン:api_tokenの形式でBasic認証を使用
     * 
     * @param apiToken APIトークン
     * @return Authorization ヘッダー値
     */
    private String createAuthorizationHeader(String apiToken) {
        String credentials = apiToken + ":api_token";
        String encodedCredentials = Base64.getEncoder()
            .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }
    
    /**
     * 時間記録統計データクラス
     */
    public record TogglTimeStats(
        int totalEntries,
        long totalDurationSeconds,
        double totalHours,
        List<TogglTimeEntryDto> timeEntries
    ) {}
}