# MCP統合仕様書

## 概要

NippogenプロジェクトにおけるModel Context Protocol (MCP) 統合の実装仕様書です。GitHub、Toggl Track、Notionなどの外部サービスとの連携をMCPを通じて行い、AI日報生成機能を実現します。

## アーキテクチャ決定

### Spring AI MCP Client採用の理由

**従来のアプローチ（ProcessBuilder + MCP Java SDK）**から**Spring AI MCP Client**に変更：

#### 比較結果
| 項目 | ProcessBuilder + MCP Java SDK | Spring AI MCP Client |
|------|------------------------------|---------------------|
| Spring Boot統合 | 手動実装が必要 | ✅ 自動設定・依存性注入 |
| 設定管理 | 手動YAML設定 | ✅ Spring Boot外部化設定 |
| Transport対応 | STDIO手動実装 | ✅ STDIO、SSE、WebFlux対応 |
| 非同期処理 | 手動実装 | ✅ 同期・非同期両対応 |
| エラーハンドリング | 全て手動 | ✅ Spring統合エラーハンドリング |
| AI統合 | 別途実装必要 | ✅ OpenAI ChatClient統合 |
| OAuth2対応 | 手動実装 | ✅ 内蔵セキュリティ機能 |
| 動的ツール更新 | 非対応 | ✅ ランタイム更新対応 |

### 技術スタック

```gradle
// Spring AI MCP Client統合
implementation 'org.springframework.ai:spring-ai-starter-mcp-client'
implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'
```

## システム全体構成

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend      │    │     Backend      │    │  External APIs  │
│  (React Native) │◄──►│   (Spring AI)    │◄──►│     (MCP)       │
│                 │    │                  │    │                 │
├─────────────────┤    ├──────────────────┤    ├─────────────────┤
│ • 日報編集      │    │ • Spring AI MCP  │    │ • GitHub MCP    │
│ • 設定管理      │    │ • OpenAI ChatClient │  │ • Toggl Track   │
│ • プレビュー    │    │ • オニオンアーキ    │    │ • Notion MCP    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## オニオンアーキテクチャ設計

```
Presentation Layer -> Application Layer -> Domain Layer <- Infrastructure Layer
     ↓                    ↓                 ↓                    ↓
Controllers        ->   UseCases      -> Repositories  <- Spring AI MCP
                                         (Interfaces)      (Implementations)
```

## MCP統合サービス

### GitHub MCP Server
- **公式リポジトリ**: https://github.com/github/github-mcp-server
- **パッケージ**: `@modelcontextprotocol/server-github`
- **実行**: `npx -y @modelcontextprotocol/server-github`
- **主要ツール**: `get_repository`, `search_issues`, `list_commits`

### Toggl Track MCP
- **リポジトリ**: https://github.com/taiseimiyaji/toggl-mcp-server
- **パッケージ**: `@taiseimiyaji/toggl-mcp-server`
- **実行**: `npx -y @taiseimiyaji/toggl-mcp-server`
- **主要ツール**: 作業時間取得、プロジェクト別集計

### Notion MCP Server
- **公式リポジトリ**: https://github.com/makenotion/notion-mcp-server
- **パッケージ**: `@makenotion/notion-mcp-server`
- **実行**: `npx -y @makenotion/notion-mcp-server`
- **主要ツール**: ページ情報取得、タスク管理情報、データベース検索

## Spring AI設定

```yaml
spring:
  ai:
    openai:
      api-key: "${OPENAI_API_KEY}"
      chat:
        model: gpt-4o-mini
    mcp:
      client:
        enabled: true
        type: SYNC
        request-timeout: 30s
        stdio:
          connections:
            github:
              command: "npx -y @modelcontextprotocol/server-github"
              env:
                GITHUB_TOKEN: "${GITHUB_TOKEN}"
            toggl:
              command: "npx -y @taiseimiyaji/toggl-mcp-server"
              env:
                TOGGL_API_TOKEN: "${TOGGL_API_TOKEN}"
            notion:
              command: "npx -y @makenotion/notion-mcp-server"
              env:
                NOTION_TOKEN: "${NOTION_TOKEN}"
```

## AI日報生成機能

### 日報生成フロー
1. **情報収集**: GitHub MCP → PR/コミット、Toggl MCP → 作業時間、Notion MCP → タスク
2. **AI処理**: OpenAI ChatClient → 情報統合・日報生成
3. **ユーザー操作**: 編集・確認・投稿

## REST APIエンドポイント

### GitHub統合API
```
GET  /api/external/github/test              # 接続テスト
GET  /api/external/github/pull-requests     # PR情報取得
GET  /api/external/github/commits           # コミット情報取得
```

### 日報生成API
```
POST /api/reports/generate                  # AI日報生成
GET  /api/reports/{date}                   # 日報取得
PUT  /api/reports/{date}                   # 日報更新
```

## 実装ステップ

### Phase 1: MCP統合基盤
- [x] ✅ Spring AI依存関係追加
- [ ] 🔄 application.yml MCP設定
- [ ] 🔄 GitHubMcpRepository Spring AI対応

### Phase 2: AI日報生成
- [ ] 📋 ReportGenerationUseCase実装
- [ ] 📋 OpenAI ChatClient統合
- [ ] 📋 日報生成API実装

### Phase 3: 統合・拡張
- [ ] 📋 Toggl Track MCP統合
- [ ] 📋 Notion MCP統合（@makenotion/notion-mcp-server）
- [ ] 📋 エラーハンドリング強化

### Phase 4: 運用機能
- [ ] 📋 自動日報生成スケジューリング
- [ ] 📋 設定管理UI
- [ ] 📋 Mattermost投稿連携

## メリット

### 開発効率
- Spring Boot外部化設定パターン
- 型安全なMCP統合
- 自動設定活用

### 機能拡張性  
- 動的ツール更新
- OAuth2統合
- マルチTransport対応

### 保守性
- Spring AIエコシステム
- オニオンアーキテクチャ
- 設定外部化

この設計により、MCP統合とAI機能が大幅に簡素化され、Spring Bootの規約に従った保守性の高い実装が実現できます。