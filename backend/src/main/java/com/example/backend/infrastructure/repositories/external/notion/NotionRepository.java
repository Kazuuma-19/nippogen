package com.example.backend.infrastructure.repositories.external.notion;

import com.example.backend.domain.external.notion.NotionPage;
import com.example.backend.domain.external.notion.NotionTableRow;
import com.example.backend.domain.external.notion.INotionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * NotionRepository の基本実装
 * 将来的にNotion API連携を実装する際の基盤クラス
 */
@Repository
public class NotionRepository implements INotionRepository {

    @Override
    public boolean testConnection() {
        // TODO: 実際のNotion API接続テストを実装
        return true;
    }

    @Override
    public NotionPage getPageContent() {
        // TODO: 実際のNotion API呼び出しを実装
        // 設定済みページIDを使用してAPIコールする
        
        return NotionPage.builder()
                .id("sample-page-id")
                .title("Sample Page")
                .content("This is sample page content")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .url("https://notion.so/sample-page-id")
                .tags(List.of("daily", "report"))
                .status("active")
                .build();
    }

    @Override
    public List<NotionTableRow> getTableContent() {
        // TODO: 実際のNotion API呼び出しを実装
        // 設定済みテーブルIDを使用してAPIコールする
        
        NotionTableRow sampleRow = NotionTableRow.builder()
                .id("sample-row-id")
                .title("Sample Row")
                .properties(Map.of(
                    "Task", "Complete implementation",
                    "Status", "In Progress",
                    "Priority", "High",
                    "Due Date", LocalDateTime.now().plusDays(3)
                ))
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now())
                .url("https://notion.so/sample-table-id#sample-row-id")
                .build();
                
        return List.of(sampleRow);
    }
}
