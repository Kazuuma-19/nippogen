-- Remove generated_content and edited_content columns from daily_reports table
ALTER TABLE daily_reports DROP COLUMN generated_content;
ALTER TABLE daily_reports DROP COLUMN edited_content;