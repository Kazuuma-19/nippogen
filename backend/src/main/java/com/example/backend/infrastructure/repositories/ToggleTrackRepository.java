package com.example.backend.infrastructure.repositories;

import com.example.backend.domain.entities.TimeEntry;
import com.example.backend.domain.repositories.IToggleTrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ToggleTrackRepository の基本実装
 * 将来的にToggleTrack API連携を実装する際の基盤クラス
 */
@Repository
@Slf4j
public class ToggleTrackRepository implements IToggleTrackRepository {

    @Override
    public boolean testConnection() {
        log.info("Testing connection to ToggleTrack");
        // TODO: 実際のToggleTrack API接続テストを実装
        return true;
    }

    @Override
    public List<TimeEntry> getTodayTimeEntries() {
        log.info("Fetching today's ToggleTrack time entries");
        // TODO: 実際のToggleTrack API呼び出しを実装
        // 今日の日付を使用してAPIコールする
        
        TimeEntry sampleEntry1 = TimeEntry.builder()
                .id(1001L)
                .description("Backend development")
                .projectName("Nippogen Project")
                .projectId(501L)
                .startTime(LocalDateTime.now().minusHours(4))
                .endTime(LocalDateTime.now().minusHours(2))
                .durationSeconds(7200L) // 2 hours
                .billable(true)
                .userId("user123")
                .workspaceName("Development Team")
                .workspaceId(101L)
                .tags(new String[]{"development", "backend"})
                .build();
        
        TimeEntry sampleEntry2 = TimeEntry.builder()
                .id(1002L)
                .description("Code review and planning")
                .projectName("Nippogen Project")
                .projectId(501L)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(null) // Currently running
                .durationSeconds(null) // Will be calculated
                .billable(true)
                .userId("user123")
                .workspaceName("Development Team")
                .workspaceId(101L)
                .tags(new String[]{"review", "planning"})
                .build();
                
        return List.of(sampleEntry1, sampleEntry2);
    }
}