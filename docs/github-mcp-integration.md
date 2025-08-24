# GitHub MCP統合仕様書

## 概要

Issue #7の実装として、GitHub公式のModel Context Protocol (MCP) Serverを活用したGitHub API統合機能を実装。日報生成のために、指定した日付のPRやコミット情報を取得する。

## GitHub MCP Server

### 基本情報
- **公式リポジトリ**: https://github.com/github/github-mcp-server
- **パッケージ名**: `@modelcontextprotocol/server-github`
- **インストール**: `npx -y @modelcontextprotocol/server-github`

### 利用可能なMCPツール

#### 1. get_repository
- **用途**: リポジトリの基本情報取得
- **パラメータ**: owner, repo
- **戻り値**: リポジトリメタデータ（名前、説明、作成日、言語等）

#### 2. search_repositories
- **用途**: リポジトリ検索
- **パラメータ**: query, sort, order
- **戻り値**: 検索結果リポジトリ一覧

#### 3. search_issues
- **用途**: Issue/PR検索
- **パラメータ**: query（検索クエリ）
- **主要クエリ例**:
  - `repo:owner/repo type:pr created:YYYY-MM-DD` : 指定日作成のPR
  - `repo:owner/repo type:issue state:open` : オープンIssue
  - `repo:owner/repo type:pr is:merged` : マージ済みPR

#### 4. list_issues
- **用途**: Issue一覧取得
- **パラメータ**: owner, repo, state, labels, sort
- **戻り値**: Issue/PR一覧

#### 5. get_issue
- **用途**: 個別Issue/PR詳細取得
- **パラメータ**: owner, repo, issue_number
- **戻り値**: Issue/PR詳細情報

#### 6. list_commits
- **用途**: コミット一覧取得
- **パラメータ**: owner, repo, since, until, path
- **戻り値**: コミット一覧

## アーキテクチャ設計

### システム構成
```
GitHubController -> GitHubMcpService -> GitHub MCP Server -> GitHub API
```

### パッケージ構造
```
com.example.backend/
├── config/
│   └── McpConfig.java           # MCP設定クラス
├── controller/
│   └── GitHubController.java    # REST APIエンドポイント
├── service/
│   └── GitHubMcpService.java    # MCP通信サービス
├── dto/
│   ├── PullRequestDto.java      # PR情報DTO
│   └── CommitDto.java           # コミット情報DTO
└── exception/
    ├── GitHubMcpException.java  # MCP専用例外
    └── GlobalExceptionHandler.java # グローバル例外ハンドラー
```

## 実装詳細

### McpConfig
```java
@Configuration
@ConfigurationProperties(prefix = "mcp.github")
@Getter
@Setter
public class McpConfig {
    private String serverExecutable = "npx -y @modelcontextprotocol/server-github";
    private String githubToken;
    private String transport = "stdio";
    private int connectionTimeout = 10000;
    private int readTimeout = 30000;
    private int maxRetries = 3;
    private int retryDelay = 2000;
}
```

### GitHubMcpService
主要メソッド：
- `testConnection(owner, repo)`: get_repositoryツール使用
- `getPullRequestsForDate(owner, repo, date)`: search_issuesツール使用
- `getCommitsForDate(owner, repo, date)`: list_commitsツール使用

### DTOクラス設計

#### PullRequestDto
```java
@Getter
@Builder
@RequiredArgsConstructor
public class PullRequestDto {
    private final Long id;
    private final Integer number;
    private final String title;
    private final String body;
    private final String state;
    private final String author;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime mergedAt;
    private final String baseBranch;
    private final String headBranch;
    private final List<String> reviewers;
    private final Integer additions;
    private final Integer deletions;
    private final Integer changedFiles;
    private final String htmlUrl;
}
```

#### CommitDto
```java
@Getter
@Builder
@RequiredArgsConstructor
public class CommitDto {
    private final String sha;
    private final String message;
    private final String author;
    private final String authorEmail;
    private final LocalDateTime date;
    private final Integer additions;
    private final Integer deletions;
    private final Integer total;
    private final String htmlUrl;
}
```

## REST APIエンドポイント

### 1. 接続テスト
```
GET /api/external/github/test
Parameters: owner, repo
Response: boolean (接続成功/失敗)
```

### 2. PR情報取得
```
GET /api/external/github/pull-requests
Parameters: owner, repo, date (YYYY-MM-DD)
Response: List<PullRequestDto>
```

### 3. コミット情報取得
```
GET /api/external/github/commits  
Parameters: owner, repo, date (YYYY-MM-DD)
Response: List<CommitDto>
```

## OpenAPI仕様

### SpringDocアノテーション
- `@Tag`: APIグループ化
- `@Operation`: エンドポイント詳細
- `@ApiResponses`: レスポンス仕様
- `@Parameter`: パラメータ説明

### 主要レスポンスコード
- `200 OK`: 正常処理
- `400 Bad Request`: パラメータエラー
- `404 Not Found`: リポジトリ未発見
- `429 Too Many Requests`: レート制限
- `500 Internal Server Error`: サーバーエラー

## エラーハンドリング

### GitHubMcpException
```java
public class GitHubMcpException extends RuntimeException {
    private final String errorCode;
    private final int statusCode;
    
    public static GitHubMcpException connectionFailed(String reason);
    public static GitHubMcpException repositoryNotFound(String owner, String repo);
    public static GitHubMcpException rateLimitExceeded(String resetTime);
    public static GitHubMcpException invalidParameters(String parameter);
}
```

### GlobalExceptionHandler
- GitHubMcpException専用ハンドリング
- パラメータ型エラー対応
- 汎用例外ハンドリング

## 設定例

### application.yml
```yaml
mcp:
  github:
    server-executable: "npx -y @modelcontextprotocol/server-github"
    github-token: "${GITHUB_TOKEN}"
    transport: "stdio"
    connection-timeout: 10000
    read-timeout: 30000
    max-retries: 3
    retry-delay: 2000
```

### 環境変数
```bash
export GITHUB_TOKEN=ghp_your_personal_access_token
```

## MCP通信実装方針

### 現在の状態
- 基本的なREST APIエンドポイント実装完了
- DTO・設定クラス・例外処理実装完了  
- 実際のMCP通信部分は未実装（TODO）

### 次のステップ（実装予定）
1. **MCPクライアント統合**: ProcessBuilderまたはMCPクライアントライブラリの選定・統合
2. **MCP通信実装**: stdio transportでのGitHub MCP Server通信
3. **レスポンス変換**: MCPレスポンスからDTO変換ロジック
4. **エラーハンドリング強化**: MCP特有のエラー対応

## テスト・動作確認

### 動作確認済み項目
✅ Spring Bootアプリケーション起動（ポート8080）  
✅ 接続テストAPI: `GET /api/external/github/test` → `true`  
✅ PR取得API: `GET /api/external/github/pull-requests` → `[]`  
✅ コミット取得API: `GET /api/external/github/commits` → `[]`  
✅ Swagger UI: `/swagger-ui.html`  
✅ PostgreSQLデータベース接続  

### GitHub APIアクセステスト済み
✅ ユーザー情報取得（Kazuuma-19）  
✅ リポジトリアクセス（nippogen）  
✅ コミット・PR情報取得  

## 完了条件
- [x] 基本的なREST APIエンドポイント
- [x] DTO・設定・例外処理クラス
- [x] OpenAPI仕様定義
- [x] アプリケーション起動・動作確認
- [ ] 実際のMCP通信実装
- [ ] GitHub APIからのデータ取得・変換
- [ ] エラーハンドリング強化
- [ ] 単体・統合テスト