-- Insert dummy user for development purposes
-- WARNING: This is for development environment only!
-- UUID: 78b955df-5c79-4514-85b0-6b2282093361

INSERT INTO users (
    id,
    email,
    password_hash,
    name,
    created_at,
    updated_at
) VALUES (
    '78b955df-5c79-4514-85b0-6b2282093361',
    'demo@nippogen.local',
    '$2a$10$dummyhashfordevelopmentpurposesonly',
    'Demo User',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO NOTHING;