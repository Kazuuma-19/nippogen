-- Drop existing api_credentials table and recreate with service-specific tables
DROP TABLE IF EXISTS api_credentials CASCADE;

-- GitHub credentials table
CREATE TABLE github_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    api_key TEXT NOT NULL,
    base_url VARCHAR(255) DEFAULT 'https://api.github.com',
    owner VARCHAR(100),
    repo VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Toggl Track credentials table
CREATE TABLE toggl_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    api_key TEXT NOT NULL,
    workspace_id BIGINT,
    project_ids INTEGER[],
    default_tags TEXT[],
    time_zone VARCHAR(50) DEFAULT 'UTC',
    include_weekends BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Notion credentials table
CREATE TABLE notion_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    api_key TEXT NOT NULL,
    database_id VARCHAR(255),
    title_property VARCHAR(100),
    status_property VARCHAR(100),
    date_property VARCHAR(100),
    filter_conditions JSONB,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for GitHub credentials
CREATE INDEX idx_github_credentials_user_id ON github_credentials(user_id);
CREATE UNIQUE INDEX idx_github_credentials_user_unique ON github_credentials(user_id) WHERE is_active = true;

-- Create indexes for Toggl credentials
CREATE INDEX idx_toggl_credentials_user_id ON toggl_credentials(user_id);
CREATE UNIQUE INDEX idx_toggl_credentials_user_unique ON toggl_credentials(user_id) WHERE is_active = true;

-- Create indexes for Notion credentials
CREATE INDEX idx_notion_credentials_user_id ON notion_credentials(user_id);
CREATE UNIQUE INDEX idx_notion_credentials_user_unique ON notion_credentials(user_id) WHERE is_active = true;

-- Apply update timestamp triggers for GitHub credentials
CREATE TRIGGER update_github_credentials_updated_at BEFORE UPDATE ON github_credentials
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Apply update timestamp triggers for Toggl credentials
CREATE TRIGGER update_toggl_credentials_updated_at BEFORE UPDATE ON toggl_credentials
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Apply update timestamp triggers for Notion credentials
CREATE TRIGGER update_notion_credentials_updated_at BEFORE UPDATE ON notion_credentials
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();