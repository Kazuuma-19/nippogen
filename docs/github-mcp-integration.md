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

## アーキテクチャ設計（オニオンアーキテクチャ準拠）

### システム構成
```
Presentation Layer -> Application Layer -> Domain Layer <- Infrastructure Layer
     ↓                    ↓                 ↓                    ↓
GitHubController -> GitHubUseCase -> GitHubRepository <- GitHubMcpRepository
                                         (Interface)         (Implementation)
```


## 実装詳細（オニオンアーキテクチャ準拠）

### Domain Layer

#### GitHubRepository (Interface)
```java
public interface GitHubRepository {
    List<PullRequest> findPullRequestsByDate(String owner, String repo, LocalDate date);
    List<GitHubCommit> findCommitsByDate(String owner, String repo, LocalDate date);
    boolean testConnection(String owner, String repo);
}
```

#### PullRequest (Entity)
```java
@Getter
@Builder
@RequiredArgsConstructor
public class PullRequest {
    private final Long id;
    private final Integer number;
    private final String title;
    private final String body;
    private final String state;
    private final String author;
    private final LocalDateTime createdAt;
    private final LocalDateTime mergedAt;
    private final String baseBranch;
    private final String headBranch;
    // ビジネスロジック
    public boolean isMerged() { return mergedAt != null; }
    public boolean isCreatedOnDate(LocalDate date) { /* 実装 */ }
}
```

### Application Layer

#### GitHubUseCase
```java
@Service
@RequiredArgsConstructor
public class GitHubUseCase {
    private final GitHubRepository gitHubRepository;
    private final GitHubResponseMapper responseMapper;
    
    public List<PullRequestDto> getPullRequestsForDate(String owner, String repo, LocalDate date) {
        List<PullRequest> pullRequests = gitHubRepository.findPullRequestsByDate(owner, repo, date);
        return responseMapper.toPullRequestDtos(pullRequests);
    }
    
    public List<CommitDto> getCommitsForDate(String owner, String repo, LocalDate date) {
        List<GitHubCommit> commits = gitHubRepository.findCommitsByDate(owner, repo, date);
        return responseMapper.toCommitDtos(commits);
    }
    
    public boolean testConnection(String owner, String repo) {
        return gitHubRepository.testConnection(owner, repo);
    }
}
```

### Infrastructure Layer

#### McpConfig
```java
@Component
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

#### GitHubMcpRepository (Implementation)
```java
@Repository
@RequiredArgsConstructor
@Slf4j
public class GitHubMcpRepository implements GitHubRepository {
    private final McpClient mcpClient;
    private final McpConfig mcpConfig;
    
    @Override
    public List<PullRequest> findPullRequestsByDate(String owner, String repo, LocalDate date) {
        // MCP search_issues ツール使用実装
    }
    
    @Override
    public List<GitHubCommit> findCommitsByDate(String owner, String repo, LocalDate date) {
        // MCP list_commits ツール使用実装
    }
    
    @Override
    public boolean testConnection(String owner, String repo) {
        // MCP get_repository ツール使用実装
    }
}
```

### Presentation Layer

#### GitHubController
```java
@RestController
@RequestMapping("/api/external/github")
@RequiredArgsConstructor
@Tag(name = "GitHub API", description = "GitHub MCP統合API")
public class GitHubController {
    private final GitHubUseCase gitHubUseCase;
    private final GitHubResponseMapper responseMapper;
    
    @GetMapping("/pull-requests")
    public ResponseEntity<List<PullRequestResponse>> getPullRequests(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<PullRequestDto> pullRequests = gitHubUseCase.getPullRequestsForDate(owner, repo, date);
        List<PullRequestResponse> responses = responseMapper.toPullRequestResponses(pullRequests);
        return ResponseEntity.ok(responses);
    }
}
```

## レイヤー別責務

### Domain Layer (Core)
- **責務**: ビジネスロジック、ドメインルール
- **依存関係**: なし（他のレイヤーに依存しない）
- **主要クラス**: 
  - `PullRequest`, `GitHubCommit` (エンティティ)
  - `GitHubRepository` (インターフェース)
  - `GitHubDomainService` (ドメインサービス)

### Application Layer  
- **責務**: ユースケース実行、アプリケーション固有のロジック
- **依存関係**: Domain Layer のみに依存
- **主要クラス**:
  - `GitHubUseCase` (ユースケース)
  - `PullRequestDto`, `CommitDto` (データ転送)

### Infrastructure Layer
- **責務**: 外部システム連携、技術的実装詳細
- **依存関係**: Domain Layer のインターフェースを実装
- **主要クラス**:
  - `GitHubMcpRepository` (リポジトリ実装)
  - `McpClient`, `McpConfig` (MCP統合)

### Presentation Layer
- **責務**: 外部からのリクエスト処理、レスポンス変換
- **依存関係**: Application Layer に依存
- **主要クラス**:
  - `GitHubController` (REST API)
  - `PullRequestResponse`, `CommitResponse` (API専用DTO)
  - `GitHubResponseMapper` (DTO変換)

## 設定管理

### McpConfig設計
`McpConfig`は`@ConfigurationProperties`でapplication.ymlの設定値を管理：

```java
@Component
@ConfigurationProperties(prefix = "mcp.github")
@Getter
@Setter
public class McpConfig {
    private String serverExecutable = "npx -y @modelcontextprotocol/server-github";
    private String githubToken;        // ${GITHUB_TOKEN}
    private String transport = "stdio";
    private int connectionTimeout = 10000;
    private int readTimeout = 30000;
    private int maxRetries = 3;
    private int retryDelay = 2000;
}
```

### application.yml
```yaml
mcp:
  github:
    server-executable: "npx -y @modelcontextprotocol/server-github"
    github-token: "${GITHUB_TOKEN}"  # 環境変数から取得
    transport: "stdio"
    connection-timeout: 10000
    read-timeout: 30000
    max-retries: 3
    retry-delay: 2000
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

### 現在の状態（オニオンアーキテクチャ移行前）
- 基本的なREST APIエンドポイント実装完了
- DTO・設定クラス・例外処理実装完了  
- 実際のMCP通信部分は未実装（TODO）

### 次のステップ（オニオンアーキテクチャ移行）
1. **Domain Layer実装**: 
   - `PullRequest`, `GitHubCommit` エンティティ作成
   - `GitHubRepository` インターフェース定義
   - ドメインサービス実装

2. **Application Layer実装**:
   - `GitHubUseCase` 作成
   - アプリケーション層のDTO定義

3. **Infrastructure Layer実装**:
   - `GitHubMcpRepository` でリポジトリ実装
   - `McpClient` でMCP通信実装
   - `McpConfig` をInfrastructure層に移動

4. **Presentation Layer実装**:
   - `GitHubController` をPresentation層に移動
   - APIレスポンス専用DTOとマッパー作成

5. **MCP通信実装**: 
   - stdio transportでのGitHub MCP Server通信
   - エラーハンドリング強化

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