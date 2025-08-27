package com.example.backend.application.usecases;

import com.example.backend.application.dto.TimeEntryDto;
import com.example.backend.domain.entities.TimeEntry;
import com.example.backend.domain.repositories.IToggleTrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ToggleTrack統合ユースケース
 * プレゼンテーション層からの要求を処理し、ドメインサービスを呼び出す
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ToggleTrackUseCase {
    
    private final IToggleTrackRepository toggleTrackRepository;
    
    /**
     * ToggleTrack接続テスト
     * 
     * @return 接続成功時true
     */
    public boolean testConnection() {
        log.info("Testing ToggleTrack connection");
        return toggleTrackRepository.testConnection();
    }
    
    /**
     * 今日の時間記録を取得
     * 
     * @return 今日の時間記録DTOのリスト
     */
    public List<TimeEntryDto> getTodayTimeEntries() {
        log.info("Fetching today's ToggleTrack time entries");
        
        List<TimeEntry> timeEntries = toggleTrackRepository.getTodayTimeEntries();
        
        return timeEntries.stream()
                .map(this::toTimeEntryDto)
                .collect(Collectors.toList());
    }
    
    /**
     * TimeEntryエンティティをTimeEntryDTOに変換
     * 
     * @param timeEntry 時間記録エンティティ
     * @return 時間記録DTO
     */
    private TimeEntryDto toTimeEntryDto(TimeEntry timeEntry) {
        return TimeEntryDto.builder()
                .id(timeEntry.getId())
                .description(timeEntry.getDescription())
                .projectName(timeEntry.getProjectName())
                .projectId(timeEntry.getProjectId())
                .startTime(timeEntry.getStartTime())
                .endTime(timeEntry.getEndTime())
                .durationSeconds(timeEntry.getDurationSeconds())
                .billable(timeEntry.isBillable())
                .userId(timeEntry.getUserId())
                .workspaceName(timeEntry.getWorkspaceName())
                .workspaceId(timeEntry.getWorkspaceId())
                .tags(timeEntry.getTags())
                .build();
    }
}