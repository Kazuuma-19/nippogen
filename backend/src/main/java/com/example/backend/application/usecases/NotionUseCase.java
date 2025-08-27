package com.example.backend.application.usecases;

import com.example.backend.application.dto.NotionPageDto;
import com.example.backend.application.dto.NotionTableRowDto;
import com.example.backend.domain.entities.NotionPage;
import com.example.backend.domain.entities.NotionTableRow;
import com.example.backend.domain.repositories.INotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notion統合ユースケース
 * プレゼンテーション層からの要求を処理し、ドメインサービスを呼び出す
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotionUseCase {
    
    private final INotionRepository notionRepository;
    
    /**
     * Notion接続テスト
     * 
     * @return 接続成功時true
     */
    public boolean testConnection() {
        log.info("Testing Notion connection");
        return notionRepository.testConnection();
    }
    
    /**
     * 設定済みページの内容を取得
     * 
     * @return ページ情報DTO
     */
    public NotionPageDto getPageContent() {
        log.info("Fetching Notion page content");
        
        NotionPage page = notionRepository.getPageContent();
        return toNotionPageDto(page);
    }
    
    /**
     * 設定済みテーブルの内容を取得
     * 
     * @return テーブル行データDTOのリスト
     */
    public List<NotionTableRowDto> getTableContent() {
        log.info("Fetching Notion table content");
        
        List<NotionTableRow> rows = notionRepository.getTableContent();
        
        return rows.stream()
                .map(this::toNotionTableRowDto)
                .collect(Collectors.toList());
    }
    
    /**
     * NotionPageエンティティをNotionPageDTOに変換
     * 
     * @param page Notionページエンティティ
     * @return NotionページDTO
     */
    private NotionPageDto toNotionPageDto(NotionPage page) {
        if (page == null) {
            return null;
        }
        
        return NotionPageDto.builder()
                .id(page.getId())
                .title(page.getTitle())
                .content(page.getContent())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .url(page.getUrl())
                .tags(page.getTags())
                .status(page.getStatus())
                .build();
    }
    
    /**
     * NotionTableRowエンティティをNotionTableRowDTOに変換
     * 
     * @param row Notionテーブル行エンティティ
     * @return Notionテーブル行DTO
     */
    private NotionTableRowDto toNotionTableRowDto(NotionTableRow row) {
        return NotionTableRowDto.builder()
                .id(row.getId())
                .title(row.getTitle())
                .properties(row.getProperties())
                .createdAt(row.getCreatedAt())
                .updatedAt(row.getUpdatedAt())
                .url(row.getUrl())
                .build();
    }
}