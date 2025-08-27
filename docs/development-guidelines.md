# 開発ガイドライン

このドキュメントでは、Nippogenプロジェクトにおける開発プロセス、コーディング規約、およびGitHubワークフローについて説明します。

## GitHub ワークフロー & 開発プロセス

### Issue 管理

- **タスク管理**: GitHub Issues を使用して全ての開発タスクとバグを追跡
- **Issue 作成**: 機能、バグ、改善のために作業開始前に Issue を作成
- **Issue テンプレート**: `.github/ISSUE_TEMPLATE/issue_template.md` にある GitHub issue テンプレートを**必須使用**
- **リンク**: コミットと PR で issue 番号を参照（例：`#123`、`fixes #456`）

### プロジェクトボード管理

- **プロジェクトボード**: https://github.com/users/Kazuuma-19/projects/14
- **カラム構造**:
  - **Backlog**: このプロジェクトで作成するすべてのタスクを追加
  - **Ready**: MVP で実装する内容
  - **In progress**: 実装中の内容
  - **Done**: 終わった内容（こちらで操作するので In Progress からは移動させない）
- **Issue 割り当て**: すべての Issue を Backlog に追加し、実装準備ができたものを Ready に移動

### コミットガイドライン

- **コミット粒度**: 単一責任の小さく集中したコミットを作成
- **コミット戦略**: **rebaseではなく、細かく複数のコミットを作成することを推奨**
  - 各論理的変更を個別のコミットに分ける
  - ファイル作成、修正、削除は可能な限り別コミットに分離
  - 機能実装とテスト追加、ドキュメント更新は別々のコミット
  - リファクタリングと新機能追加は別々のコミット
- **言語**: コミットメッセージは日本語で記述
- **コミットメッセージ**: 日本語説明付きの conventional commit 形式に従う:

  ```
  type(scope): 日本語での説明

  - feat: 新機能
  - fix: バグ修正
  - docs: ドキュメント変更
  - style: フォーマット変更
  - refactor: リファクタリング
  - test: テスト追加
  - chore: 保守作業
  ```

- **例**:
  ```
  feat(api): ユーザー認証エンドポイントを追加
  fix(frontend): ログインページのナビゲーション問題を修正
  docs(readme): インストール手順を更新
  refactor(backend): データベース接続処理をリファクタリング
  ```

- **推奨コミットパターン**:
  ```
  # 良い例：論理的に分離された複数コミット
  feat(domain): PullRequestエンティティを追加
  feat(domain): GitHubRepositoryインターフェースを追加
  feat(application): GitHubUseCaseを実装
  feat(infrastructure): GitHubMcpRepositoryを実装
  feat(presentation): GitHubControllerを実装
  test(github): GitHubUseCase単体テストを追加
  docs(github): GitHub MCP統合仕様書を更新

  # 避ける例：すべてを1つのコミットにまとめる
  feat(github): GitHub MCP統合機能を完全実装
  ```

### プルリクエストプロセス

- **言語**: PR タイトルと説明は日本語で記述
- **PR テンプレート**: `.github/pull_request_template.md` にある GitHub PR テンプレートを**必須使用**
- **ブランチ命名**: わかりやすいブランチ名を使用（例：`feature/user-auth`、`fix/login-bug`）
- **レビュー要件**:
  - 全 PR は @Kazuuma-19 によるレビュー必須
  - PR をセルフマージしない - レビュー承認を待つ
  - 再レビュー要求前に全レビューコメントに対応
- **PR 作成**: 常に以下を含める:
  - **変更内容**: 日本語での変更の明確な説明
  - **関連 Issue**: 関連 issue への参照
  - **実装の思考プロセス**: 実装決定の理由説明
    - なぜこのアプローチを選択したか
    - 他の選択肢と比較検討した内容
    - 実装時に考慮した点や制約
    - 将来の拡張性や保守性への配慮
  - **テスト手順**: テスト手順
  - **スクリーンショット**: UI 変更のスクリーンショット/録画
  - **影響範囲**: 変更の範囲と潜在的影響

### ドキュメントガイドライン

- **ドキュメント場所**: プロジェクトドキュメントは全て `/docs` ディレクトリ下に作成
- **開発前レビュー**: 開発開始前に `/docs` の既存ドキュメントを必ずレビュー
- **ドキュメント種別**:
  - アーキテクチャ決定記録（ADR）
  - API 仕様
  - セットアップガイド
  - トラブルシューティングガイド
  - 設計書

### 開発フロー

1. **Issue 作成 & 計画**:

   - **必須**テンプレート `.github/ISSUE_TEMPLATE/issue_template.md` を使用して GitHub Issue を作成
   - チェックボックスを使用して具体的なサブタスクに分解:

     ```markdown
     ## タスク一覧

     - [ ] データベースマイグレーション作成
     - [ ] API エンドポイント実装
     - [ ] フロントエンド画面作成
     - [ ] テストコード追加
     - [ ] ドキュメント更新
     ```

   - 適切なラベルとマイルストーンを割り当て

2. **開発前準備**:

   - 関連ドキュメントについて `/docs` ディレクトリをレビュー
   - 既存のアーキテクチャとパターンを理解
   - **実装前に main からフィーチャーブランチを必ず作成**
   - **GitHub Projects で該当 Issue のステータスを "In progress" に変更**

3. **実装**:

   - **フィーチャーブランチで常に作業、main で直接作業しない**
   - サブタスクを体系的に処理
   - 完了したサブタスクを issue で ✅ でチェックオフ
   - 日本語で細かいコミットを作成
   - 必要に応じてドキュメントを更新

4. **完了**:
   - 全てのサブタスクがチェックオフされていることを確認
   - **必須**テンプレート `.github/pull_request_template.md` を使用して PR を作成
   - @Kazuuma-19 にレビューを要求
   - フィードバックに対応し承認を待つ

### コードレビュープロセス

1. **実装前に main からフィーチャーブランチを必ず作成**
2. 開発前に `/docs` ディレクトリをレビュー
3. **GitHub Projects で該当 Issue のステータスを "In progress" に変更**
4. **フィーチャーブランチで常に作業、main で直接作業しない**
5. 細かいコミットで変更を実装
6. タスク完了チェックマークで issue を更新
7. **必須**テンプレート `.github/pull_request_template.md` を使用して PR を作成
8. @Kazuuma-19 にレビューを要求
9. レビューフィードバックに対応
10. マージ前に承認を待つ
11. **自分の PR をマージしない**

### ブランチ命名規則

- **形式**: `feature/issue-number-short-description`
- **例**:
  - `feature/6-database-migration`
  - `feature/10-ai-report-generation`
  - `feature/14-api-settings-screen`

## Lombokガイドライン

### 使用推奨アノテーション
- `@Getter` / `@Setter`: フィールドレベルまたはクラスレベルでgetter/setter生成
- `@Builder`: Builderパターン実装
- `@RequiredArgsConstructor`: final/非nullフィールドのコンストラクタ生成
- `@AllArgsConstructor`: 全フィールドのコンストラクタ生成
- `@NoArgsConstructor`: デフォルトコンストラクタ生成
- `@Slf4j`: ログ用
- `@ToString`: toString()メソッド生成
- `@EqualsAndHashCode`: equals()とhashCode()メソッド生成

### 使用禁止アノテーション
- `@Data`: mutabilityとtoString/hashCode/equalsの自動生成によるリスクがあるため使用禁止

### DTOクラス設計原則
- イミュータブルオブジェクト推奨: `@Getter + @Builder + @RequiredArgsConstructor + final fields`
- ConfigurationPropertiesクラス: `@Getter + @Setter` (Spring Bootの要件)

### Dependency Injection
- **推奨**: `@RequiredArgsConstructor` を使用したコンストラクタインジェクション
  - final フィールドでの依存関係宣言
  - 不変性とテスタビリティの向上
  - Spring推奨パターンに準拠
- **例**:
  ```java
  @Service
  @RequiredArgsConstructor
  public class UserService {
      private final UserRepository userRepository;
      private final EmailService emailService;
  }
  ```

## 外部API統合のベストプラクティス

### GitHub API統合の実装パターン

プロジェクトで実際に経験した**MCP → REST API直接実装**への移行から得られた教訓：

#### 選択基準
1. **安定性重視**: 成熟したREST APIを選択
2. **シンプルさ**: 複雑なプロトコルスタックを避ける  
3. **デバッグ容易性**: HTTP通信で問題を特定しやすく
4. **長期保守性**: 公式APIの長期サポート保証

#### 実装構造
```java
infrastructure/
├── github/
│   ├── client/GitHubRestApiClient.java      # WebClient利用
│   ├── config/GitHubApiConfiguration.java   # Bean設定
│   └── dto/                                 # レスポンスDTO群
└── repositories/GitHubRestApiRepository.java # ドメイン変換
```

#### 設定例（application.yml）
```yaml
spring:
  application:
    github:
      api:
        base-url: https://api.github.com
        token: ${GITHUB_TOKEN}
        timeout: 30s
```

### MCP統合で学んだ失敗パターン

#### 発生した問題
- **バージョン互換性**: Spring AI MCP Client vs GitHub MCP Server のスキーマ不整合
- **Docker複雑性**: コンテナ内Docker実行の権限問題
- **デバッグ困難**: プロトコルスタック多層化による問題切り分けの難しさ
- **プロジェクト廃止**: 実験的プロジェクトの突然のアーカイブ化

#### 教訓
1. **プロトコル選択**: 新しいプロトコルより成熟したREST APIを選択
2. **依存関係**: 実験的プロジェクトへの依存を避ける
3. **シンプルさ**: 複雑な設定よりも直接的な実装を優先
4. **トラブルシュート**: HTTP/JSONレベルでデバッグできる構成を選択

### 外部API統合の推奨パターン

#### 1. WebClient利用パターン
```java
@Configuration
public class ApiConfiguration {
    @Bean
    public WebClient apiWebClient() {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Bearer " + token)
            .build();
    }
}
```

#### 2. DTO分離パターン
- 外部API専用DTOと内部ドメインエンティティを分離
- 変換ロジックをRepository層で実装
- 外部APIの変更がドメイン層に影響しない設計

#### 3. エラーハンドリング
- `onErrorMap()` で外部API例外を内部例外に変換
- リトライ機構とサーキットブレーカーの実装検討
- ログレベルを適切に設定（外部API呼び出しの可視化）

## トラブルシューティングガイド

### よくある問題と解決策

#### 1. ビルドエラー
- **JOOQクラス未生成**: `./gradlew generateJooq` 実行
- **OpenAPI型不整合**: `npm run generate-types` でフロントエンド型を再生成
- **依存関係競合**: `./gradlew dependencies` で依存関係ツリーを確認

#### 2. 外部API接続問題
- **認証エラー**: 環境変数の設定確認（`.env`ファイル）
- **レート制限**: APIリクエスト頻度を調整
- **ネットワークエラー**: Docker環境でのポート設定確認

#### 3. データベース問題
- **マイグレーション失敗**: Flyway状態を確認し、必要に応じて修正
- **JOOQ生成失敗**: データベースとアプリケーションの接続確認

これらの経験を活かし、将来の外部API統合では**REST API直接実装**パターンを標準とする。