# Nippogen API仕様書

## 基本情報
- Base URL: `http://localhost:8080/api`
- Content-Type: `application/json`
- 認証: JWT Bearer Token

## エンドポイント一覧

### 認証関連

#### POST /auth/register
ユーザー登録
```json
Request:
{
  "email": "user@example.com",
  "password": "password123",
  "name": "山田太郎"
}

Response: 201 Created
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "山田太郎",
  "token": "jwt_token"
}
```

#### POST /auth/login
ログイン
```json
Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "山田太郎",
  "token": "jwt_token"
}
```

### API認証情報管理

#### GET /api/credentials
認証情報一覧取得
```json
Response: 200 OK
[
  {
    "id": "uuid",
    "serviceType": "GITHUB",
    "isActive": true,
    "additionalConfig": {},
    "createdAt": "2025-01-01T00:00:00Z"
  }
]
```

#### POST /api/credentials
認証情報登録
```json
Request:
{
  "serviceType": "GITHUB",
  "apiKey": "ghp_xxxxxxxxxxxx",
  "additionalConfig": {
    "organization": "company-name"
  }
}

Response: 201 Created
{
  "id": "uuid",
  "serviceType": "GITHUB",
  "isActive": true,
  "createdAt": "2025-01-01T00:00:00Z"
}
```

#### PUT /api/credentials/{id}
認証情報更新
```json
Request:
{
  "apiKey": "ghp_yyyyyyyyyyyy",
  "additionalConfig": {
    "organization": "new-company"
  }
}

Response: 200 OK
{
  "id": "uuid",
  "serviceType": "GITHUB",
  "isActive": true,
  "updatedAt": "2025-01-01T00:00:00Z"
}
```

#### DELETE /api/credentials/{id}
認証情報削除
```json
Response: 204 No Content
```

### 外部サービス連携

#### GET /api/external/github/test
GitHub接続テスト
```json
Response: 200 OK
{
  "connected": true,
  "username": "github-user",
  "message": "Successfully connected to GitHub"
}
```

#### GET /api/external/toggl/test
Toggl Track接続テスト
```json
Response: 200 OK
{
  "connected": true,
  "workspaces": ["Workspace 1", "Workspace 2"],
  "message": "Successfully connected to Toggl Track"
}
```

#### GET /api/external/notion/test
Notion接続テスト
```json
Response: 200 OK
{
  "connected": true,
  "databases": ["Task DB", "Notes DB"],
  "message": "Successfully connected to Notion"
}
```

### 日報管理

#### GET /api/reports
日報一覧取得
```json
Query Parameters:
- from: 2025-01-01 (開始日)
- to: 2025-01-31 (終了日)
- status: DRAFT|EDITED|APPROVED

Response: 200 OK
[
  {
    "id": "uuid",
    "reportDate": "2025-01-01",
    "status": "APPROVED",
    "generationCount": 2,
    "createdAt": "2025-01-01T17:45:00Z",
    "updatedAt": "2025-01-01T18:00:00Z"
  }
]
```

#### GET /api/reports/{date}
特定日の日報取得
```json
Response: 200 OK
{
  "id": "uuid",
  "reportDate": "2025-01-01",
  "rawData": {
    "github": {...},
    "toggl": {...},
    "notion": {...}
  },
  "generatedContent": "## 今日やったこと\n...",
  "editedContent": "## 今日やったこと\n...",
  "finalContent": "## 今日やったこと\n...",
  "status": "APPROVED",
  "generationCount": 1,
  "additionalNotes": "会議メモ...",
  "createdAt": "2025-01-01T17:45:00Z"
}
```

#### POST /api/reports/generate
日報生成
```json
Request:
{
  "date": "2025-01-01",
  "additionalNotes": "今日は新機能の実装を中心に作業しました"
}

Response: 201 Created
{
  "id": "uuid",
  "reportDate": "2025-01-01",
  "generatedContent": "## 今日やったこと\n...",
  "status": "DRAFT",
  "generationCount": 1
}
```

#### POST /api/reports/{id}/regenerate
日報再生成
```json
Request:
{
  "additionalNotes": "技術的な詳細を追加してください",
  "feedback": "もっと具体的に書いてください"
}

Response: 200 OK
{
  "id": "uuid",
  "generatedContent": "## 今日やったこと\n...",
  "generationCount": 2
}
```

#### PUT /api/reports/{id}
日報編集
```json
Request:
{
  "editedContent": "## 今日やったこと\n編集済み内容...",
  "status": "EDITED"
}

Response: 200 OK
{
  "id": "uuid",
  "editedContent": "## 今日やったこと\n編集済み内容...",
  "status": "EDITED",
  "updatedAt": "2025-01-01T18:00:00Z"
}
```

#### POST /api/reports/{id}/approve
日報承認
```json
Request:
{
  "finalContent": "## 今日やったこと\n最終版..."
}

Response: 200 OK
{
  "id": "uuid",
  "status": "APPROVED",
  "finalContent": "## 今日やったこと\n最終版...",
  "updatedAt": "2025-01-01T18:00:00Z"
}
```

#### GET /api/reports/{id}/export
日報エクスポート（Markdown）
```json
Response: 200 OK
Content-Type: text/markdown

## 2025年1月1日 日報

### 今日やったこと
- PR #123 レビュー対応
- 新機能実装（3時間）

### 所感
- テストコードの重要性を実感

### 学んだこと
- React hooksの使い方

### 次やること
- リファクタリング
```

### テンプレート管理

#### GET /api/templates
テンプレート一覧取得
```json
Response: 200 OK
[
  {
    "id": "uuid",
    "name": "標準テンプレート",
    "isDefault": true,
    "formatStructure": {
      "sections": [
        {"title": "今日やったこと", "key": "tasks"},
        {"title": "所感", "key": "thoughts"},
        {"title": "学んだこと", "key": "learnings"},
        {"title": "次やること", "key": "next"}
      ]
    }
  }
]
```

#### POST /api/templates
テンプレート作成
```json
Request:
{
  "name": "カスタムテンプレート",
  "formatStructure": {
    "sections": [...]
  },
  "isDefault": false
}

Response: 201 Created
{
  "id": "uuid",
  "name": "カスタムテンプレート",
  "isDefault": false
}
```

## エラーレスポンス

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Invalid request parameters",
  "details": ["email is required"]
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Report not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```