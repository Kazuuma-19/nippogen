# Nippogen データベース設計

## テーブル一覧

### 1. users (ユーザー)
| カラム名 | 型 | 制約 | 説明 |
|---------|-----|------|------|
| id | UUID | PRIMARY KEY | ユーザーID |
| email | VARCHAR(255) | UNIQUE, NOT NULL | メールアドレス |
| password_hash | VARCHAR(255) | NOT NULL | パスワードハッシュ |
| name | VARCHAR(100) | NOT NULL | ユーザー名 |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

### 2. api_credentials (API認証情報)
| カラム名 | 型 | 制約 | 説明 |
|---------|-----|------|------|
| id | UUID | PRIMARY KEY | 認証情報ID |
| user_id | UUID | FOREIGN KEY | ユーザーID |
| service_type | VARCHAR(50) | NOT NULL | サービス種別(GITHUB/TOGGL/NOTION) |
| encrypted_key | TEXT | NOT NULL | 暗号化されたAPIキー |
| additional_config | JSONB | | 追加設定(Notion DBのIDなど) |
| is_active | BOOLEAN | DEFAULT true | 有効フラグ |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

### 3. daily_reports (日報)
| カラム名 | 型 | 制約 | 説明 |
|---------|-----|------|------|
| id | UUID | PRIMARY KEY | 日報ID |
| user_id | UUID | FOREIGN KEY | ユーザーID |
| report_date | DATE | NOT NULL | 日報対象日 |
| raw_data | JSONB | | 収集した生データ |
| generated_content | TEXT | | AI生成された日報内容 |
| edited_content | TEXT | | 編集後の日報内容 |
| final_content | TEXT | | 最終的な日報内容 |
| status | VARCHAR(20) | NOT NULL | ステータス(DRAFT/EDITED/APPROVED) |
| generation_count | INTEGER | DEFAULT 0 | 生成回数 |
| additional_notes | TEXT | | ユーザー追加情報 |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

### 4. report_templates (日報テンプレート)
| カラム名 | 型 | 制約 | 説明 |
|---------|-----|------|------|
| id | UUID | PRIMARY KEY | テンプレートID |
| user_id | UUID | FOREIGN KEY | ユーザーID |
| name | VARCHAR(100) | NOT NULL | テンプレート名 |
| format_structure | JSONB | NOT NULL | フォーマット構造 |
| is_default | BOOLEAN | DEFAULT false | デフォルトフラグ |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

### 5. generation_logs (生成ログ)
| カラム名 | 型 | 制約 | 説明 |
|---------|-----|------|------|
| id | UUID | PRIMARY KEY | ログID |
| report_id | UUID | FOREIGN KEY | 日報ID |
| generation_number | INTEGER | NOT NULL | 生成回数 |
| prompt | TEXT | | 使用したプロンプト |
| model_response | TEXT | | AIのレスポンス |
| user_feedback | TEXT | | ユーザーフィードバック |
| created_at | TIMESTAMP | NOT NULL | 生成日時 |

## インデックス

```sql
-- users
CREATE INDEX idx_users_email ON users(email);

-- api_credentials
CREATE INDEX idx_api_credentials_user_id ON api_credentials(user_id);
CREATE INDEX idx_api_credentials_service_type ON api_credentials(service_type);
CREATE UNIQUE INDEX idx_api_credentials_user_service ON api_credentials(user_id, service_type);

-- daily_reports
CREATE INDEX idx_daily_reports_user_id ON daily_reports(user_id);
CREATE INDEX idx_daily_reports_report_date ON daily_reports(report_date);
CREATE UNIQUE INDEX idx_daily_reports_user_date ON daily_reports(user_id, report_date);

-- report_templates
CREATE INDEX idx_report_templates_user_id ON report_templates(user_id);

-- generation_logs
CREATE INDEX idx_generation_logs_report_id ON generation_logs(report_id);
```

## 制約

1. 1ユーザーにつき、1つのサービスタイプに対して1つのAPI認証情報のみ
2. 1ユーザーにつき、1日1つの日報のみ
3. デフォルトテンプレートは1ユーザーにつき1つまで

## セキュリティ考慮事項

1. `api_credentials.encrypted_key`は必ず暗号化して保存
2. `password_hash`はbcryptまたはargon2でハッシュ化
3. 個人情報を含む可能性のあるJSONBフィールドは適切にサニタイズ