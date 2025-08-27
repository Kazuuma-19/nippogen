package com.example.backend.infrastructure.repositories;

import com.example.backend.domain.entities.DailyReport;
import com.example.backend.domain.enums.ReportStatus;
import com.example.backend.domain.repositories.IDailyReportRepository;
import com.example.backend.jooq.tables.JDailyReports;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jooq.JSONB;

/**
 * 日報リポジトリのJOOQ実装
 * PostgreSQLデータベースとJOOQを使用した日報データアクセス
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DailyReportRepository implements IDailyReportRepository {
    
    private final DSLContext dsl;
    private static final JDailyReports dailyReports = JDailyReports.dailyReports;
    
    @Override
    public DailyReport save(DailyReport report) {
        log.info("Saving daily report: {}", report.getId());
        
        if (existsById(report.getId())) {
            return update(report);
        } else {
            return insert(report);
        }
    }
    
    @Override
    public Optional<DailyReport> findById(UUID id) {
        log.debug("Finding daily report by id: {}", id);
        
        return dsl.selectFrom(dailyReports)
                .where(dailyReports.ID.eq(id))
                .fetchOptional()
                .map(this::mapToEntity);
    }
    
    @Override
    public Optional<DailyReport> findByUserIdAndDate(UUID userId, LocalDate reportDate) {
        log.debug("Finding daily report by userId: {} and date: {}", userId, reportDate);
        
        return dsl.selectFrom(dailyReports)
                .where(dailyReports.USER_ID.eq(userId)
                       .and(dailyReports.REPORT_DATE.eq(reportDate)))
                .fetchOptional()
                .map(this::mapToEntity);
    }
    
    @Override
    public List<DailyReport> findByUserId(UUID userId) {
        log.debug("Finding daily reports by userId: {}", userId);
        
        return dsl.selectFrom(dailyReports)
                .where(dailyReports.USER_ID.eq(userId))
                .orderBy(dailyReports.REPORT_DATE.desc())
                .fetch()
                .map(this::mapToEntity);
    }
    
    @Override
    public List<DailyReport> findByUserIdAndDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding daily reports by userId: {} and date range: {} to {}", userId, startDate, endDate);
        
        return dsl.selectFrom(dailyReports)
                .where(dailyReports.USER_ID.eq(userId)
                       .and(dailyReports.REPORT_DATE.between(startDate, endDate)))
                .orderBy(dailyReports.REPORT_DATE.desc())
                .fetch()
                .map(this::mapToEntity);
    }
    
    @Override
    public List<DailyReport> findByUserIdAndStatus(UUID userId, ReportStatus status) {
        log.debug("Finding daily reports by userId: {} and status: {}", userId, status);
        
        return dsl.selectFrom(dailyReports)
                .where(dailyReports.USER_ID.eq(userId)
                       .and(dailyReports.STATUS.eq(status.getValue())))
                .orderBy(dailyReports.REPORT_DATE.desc())
                .fetch()
                .map(this::mapToEntity);
    }
    
    @Override
    public List<DailyReport> findByUserIdAndDateRangeAndStatus(UUID userId, LocalDate startDate, LocalDate endDate, ReportStatus status) {
        log.debug("Finding daily reports by userId: {}, date range: {} to {}, and status: {}", userId, startDate, endDate, status);
        
        return dsl.selectFrom(dailyReports)
                .where(dailyReports.USER_ID.eq(userId)
                       .and(dailyReports.REPORT_DATE.between(startDate, endDate))
                       .and(dailyReports.STATUS.eq(status.getValue())))
                .orderBy(dailyReports.REPORT_DATE.desc())
                .fetch()
                .map(this::mapToEntity);
    }
    
    @Override
    public boolean deleteById(UUID id) {
        log.info("Deleting daily report by id: {}", id);
        
        int deletedCount = dsl.deleteFrom(dailyReports)
                .where(dailyReports.ID.eq(id))
                .execute();
        
        return deletedCount > 0;
    }
    
    @Override
    public boolean existsById(UUID id) {
        log.debug("Checking existence of daily report by id: {}", id);
        
        return dsl.selectCount()
                .from(dailyReports)
                .where(dailyReports.ID.eq(id))
                .fetchOne(0, int.class) > 0;
    }
    
    @Override
    public boolean existsByUserIdAndDate(UUID userId, LocalDate reportDate) {
        log.debug("Checking existence of daily report by userId: {} and date: {}", userId, reportDate);
        
        return dsl.selectCount()
                .from(dailyReports)
                .where(dailyReports.USER_ID.eq(userId)
                       .and(dailyReports.REPORT_DATE.eq(reportDate)))
                .fetchOne(0, int.class) > 0;
    }
    
    @Override
    public long countByUserId(UUID userId) {
        log.debug("Counting daily reports by userId: {}", userId);
        
        return dsl.selectCount()
                .from(dailyReports)
                .where(dailyReports.USER_ID.eq(userId))
                .fetchOne(0, long.class);
    }
    
    @Override
    public long countByUserIdAndStatus(UUID userId, ReportStatus status) {
        log.debug("Counting daily reports by userId: {} and status: {}", userId, status);
        
        return dsl.selectCount()
                .from(dailyReports)
                .where(dailyReports.USER_ID.eq(userId)
                       .and(dailyReports.STATUS.eq(status.getValue())))
                .fetchOne(0, long.class);
    }
    
    /**
     * 新しい日報レコードを挿入
     * 
     * @param report 日報エンティティ
     * @return 挿入された日報エンティティ
     */
    private DailyReport insert(DailyReport report) {
        log.debug("Inserting new daily report: {}", report.getId());
        
        dsl.insertInto(dailyReports)
                .set(dailyReports.ID, report.getId())
                .set(dailyReports.USER_ID, report.getUserId())
                .set(dailyReports.REPORT_DATE, report.getReportDate())
                .set(dailyReports.RAW_DATA, report.getRawData() != null ? JSONB.valueOf(report.getRawData()) : null)
                .set(dailyReports.GENERATED_CONTENT, report.getGeneratedContent())
                .set(dailyReports.EDITED_CONTENT, report.getEditedContent())
                .set(dailyReports.FINAL_CONTENT, report.getFinalContent())
                .set(dailyReports.STATUS, report.getStatus().getValue())
                .set(dailyReports.GENERATION_COUNT, report.getGenerationCount())
                .set(dailyReports.ADDITIONAL_NOTES, report.getAdditionalNotes())
                .set(dailyReports.CREATED_AT, report.getCreatedAt())
                .set(dailyReports.UPDATED_AT, report.getUpdatedAt())
                .execute();
        
        return report;
    }
    
    /**
     * 既存の日報レコードを更新
     * 
     * @param report 日報エンティティ
     * @return 更新された日報エンティティ
     */
    private DailyReport update(DailyReport report) {
        log.debug("Updating existing daily report: {}", report.getId());
        
        dsl.update(dailyReports)
                .set(dailyReports.RAW_DATA, report.getRawData() != null ? JSONB.valueOf(report.getRawData()) : null)
                .set(dailyReports.GENERATED_CONTENT, report.getGeneratedContent())
                .set(dailyReports.EDITED_CONTENT, report.getEditedContent())
                .set(dailyReports.FINAL_CONTENT, report.getFinalContent())
                .set(dailyReports.STATUS, report.getStatus().getValue())
                .set(dailyReports.GENERATION_COUNT, report.getGenerationCount())
                .set(dailyReports.ADDITIONAL_NOTES, report.getAdditionalNotes())
                .set(dailyReports.UPDATED_AT, LocalDateTime.now())
                .where(dailyReports.ID.eq(report.getId()))
                .execute();
        
        return report.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * JOOQレコードをDailyReportエンティティにマッピング
     * 
     * @param record JOOQレコード
     * @return DailyReportエンティティ
     */
    private DailyReport mapToEntity(org.jooq.Record record) {
        return DailyReport.builder()
                .id(record.get(dailyReports.ID))
                .userId(record.get(dailyReports.USER_ID))
                .reportDate(record.get(dailyReports.REPORT_DATE))
                .rawData(record.get(dailyReports.RAW_DATA) != null ? record.get(dailyReports.RAW_DATA).toString() : null)
                .generatedContent(record.get(dailyReports.GENERATED_CONTENT))
                .editedContent(record.get(dailyReports.EDITED_CONTENT))
                .finalContent(record.get(dailyReports.FINAL_CONTENT))
                .status(ReportStatus.fromValue(record.get(dailyReports.STATUS)))
                .generationCount(record.get(dailyReports.GENERATION_COUNT))
                .additionalNotes(record.get(dailyReports.ADDITIONAL_NOTES))
                .createdAt(record.get(dailyReports.CREATED_AT))
                .updatedAt(record.get(dailyReports.UPDATED_AT))
                .build();
    }
}
