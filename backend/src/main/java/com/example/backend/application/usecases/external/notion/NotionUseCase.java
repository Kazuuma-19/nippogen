package com.example.backend.application.usecases.external.notion;

import com.example.backend.application.dto.external.notion.NotionPageDto;
import com.example.backend.application.dto.external.notion.NotionTableRowDto;
import com.example.backend.domain.external.notion.NotionPage;
import com.example.backend.domain.external.notion.NotionTableRow;
import com.example.backend.domain.external.notion.INotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notion統合ユースケース
 * プレゼンテーション層からの要求を処理し、ドメインサービスを呼び出す
 */
@Service
@RequiredArgsConstructor
public class NotionUseCase {
    
    private final INotionRepository notionRepository;
    
    /**
     * Notion接続テスト
     * 
     * @return 接続成功時true
     */
    public boolean testConnection() {
        return notionRepository.testConnection();
    }
    
    /**
     * 設定済みページの内容を取得
     * 
     * @return ページ情報DTO
     */
    public NotionPageDto getPageContent() {
        
        NotionPage page = notionRepository.getPageContent();
        return toNotionPageDto(page);
    }
    
    /**
     * 設定済みテーブルの内容を取得
     * 
     * @return テーブル行データDTOのリスト
     */
    public List<NotionTableRowDto> getTableContent() {
        
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
