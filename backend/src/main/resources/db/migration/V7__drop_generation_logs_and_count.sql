-- Drop generation_logs table and generation_count column from daily_reports
DROP TABLE IF EXISTS generation_logs;
ALTER TABLE daily_reports DROP COLUMN IF EXISTS generation_count;