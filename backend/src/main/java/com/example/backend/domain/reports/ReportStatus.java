package com.example.backend.domain.reports;

/**
 * 日報のステータスを表すenum
 * データベースのCHECK制約と対応
 */
public enum ReportStatus {
    /**
     * 下書き状態
     */
    DRAFT("DRAFT"),
    
    /**
     * 編集済み状態
     */
    EDITED("EDITED"),
    
    /**
     * 承認済み状態
     */
    APPROVED("APPROVED");
    
    private final String value;
    
    ReportStatus(String value) {
        this.value = value;
    }
    
    /**
     * データベース保存用の値を取得
     * 
     * @return データベース用の文字列値
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 文字列からenumに変換
     * 
     * @param value 文字列値
     * @return 対応するReportStatus
     * @throws IllegalArgumentException 無効な値の場合
     */
    public static ReportStatus fromValue(String value) {
        for (ReportStatus status : ReportStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ReportStatus value: " + value);
    }
    
    /**
     * ドラフト状態かチェック
     * 
     * @return ドラフト状態の場合true
     */
    public boolean isDraft() {
        return this == DRAFT;
    }
    
    /**
     * 編集済み状態かチェック
     * 
     * @return 編集済み状態の場合true
     */
    public boolean isEdited() {
        return this == EDITED;
    }
    
    /**
     * 承認済み状態かチェック
     * 
     * @return 承認済み状態の場合true
     */
    public boolean isApproved() {
        return this == APPROVED;
    }
    
    /**
     * 編集可能な状態かチェック
     * 
     * @return 編集可能な場合true（DRAFT または EDITED）
     */
    public boolean isEditable() {
        return this == DRAFT || this == EDITED;
    }
}
