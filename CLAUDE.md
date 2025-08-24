# CLAUDE.md

このファイルは、このリポジトリでコード作業を行う際のClaude Code (claude.ai/code) に対するガイダンスを提供します。

## プロジェクト構成

Nippogenは、フロントエンドとバックエンドを明確に分離したフルスタックアプリケーションです：

### バックエンド (Spring Boot + Java 21)

- **フレームワーク**: Spring Boot 3.5.4 with Java 21
- **データベース**: PostgreSQL with JOOQ for type-safe database operations
- **データベースマイグレーション**: Flyway for database schema management
- **API ドキュメント**: SpringDoc OpenAPI with Swagger UI
- **ビルドツール**: Gradle with wrapper
- **場所**: `/backend/` ディレクトリ

### フロントエンド (React Native + Expo)

- **フレームワーク**: Expo with React Native 0.79.5 and React 19.0.0
- **スタイリング**: TailwindCSS with NativeWind for React Native
- **ルーティング**: Expo Router with file-based routing
- **HTTP クライアント**: Axios for API communication
- **TypeScript**: Full TypeScript support with strict mode
- **場所**: `/frontend/` ディレクトリ

### 型安全性 & API 統合

- バックエンドJavaアノテーションからSpringDocによるOpenAPI仕様生成
- `openapi-typescript`経由でOpenAPI仕様からTypeScript型を自動生成
- 生成された型は`frontend/types/api.ts`に配置

## 開発コマンド

### プロジェクトセットアップ

```bash
# 全依存関係のインストール（フロントエンド・バックエンド）
make ready

# フロントエンドのみセットアップ
make front-ready

# バックエンドのみセットアップ
make back-ready
```

### 開発ワークフロー

```bash
# フロントエンド開発サーバー開始（モバイルテスト用トンネル付き）
cd frontend && npm run start

# または make コマンド使用
make front

# バックエンドをDockerでビルド・開始
make back

# バックエンドビルドのみ
make build
```

### データベース操作

```bash
# PostgreSQL コンテナ開始
make up

# PostgreSQL シェルアクセス
make psql

# コンテナ停止
make down

# コンテナ停止・データベースボリューム削除
make down-v
```

### コード品質 & 型

```bash
# フロントエンドlinting
cd frontend && npm run lint

# OpenAPI仕様からTypeScript型生成
cd frontend && npm run generate-types

# バックエンドテスト
cd backend && ./gradlew test

# バックエンドビルド（テスト含む）
cd backend && ./gradlew build
```

### データベース & JOOQ

```bash
# データベースマイグレーション実行
make migrate
# または: cd backend && ./gradlew flywayMigrate

# JOOQ クラス生成（Flywayマイグレーションを自動実行）
make jooq
# または: cd backend && ./gradlew generateJooq

# マイグレーション + JOOQ生成を一括実行
make db-setup
```

## プロジェクト構造

### バックエンド構造

- `src/main/java/com/example/backend/` - メインアプリケーションコード
- `src/main/resources/db/migration/` - Flyway マイグレーションファイル
- `src/main/resources/application.yml` - Spring 設定
- `build/generated-src/jooq/main/` - 生成された JOOQ クラス
- `openapi.json` - 生成された OpenAPI 仕様

### フロントエンド構造

- `app/` - Expo Router pages (file-based routing)
- `types/api.ts` - バックエンド OpenAPI 仕様から生成された TypeScript 型
- `global.css` - グローバル Tailwind CSS スタイル

## データベース設定

### 開発データベース

- **Host**: localhost:5433 (ローカル PostgreSQL との競合を避けるため)
- **Database**: nippogen
- **User**: postgres
- **Password**: postgres

### コンテナデータベース（バックエンド接続用）

- **Host**: db:5432
- **Database**: nippogen
- **User**: postgres
- **Password**: postgres

## API ドキュメント

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 主要開発パターン

### バックエンド開発

- API ドキュメントには SpringDoc アノテーション（`@Operation`、`@Tag`）を使用
- データベース変更は `src/main/resources/db/migration/` の Flyway マイグレーションで実行
- スキーマ変更後は `./gradlew generateJooq` で JOOQ クラスを再生成
- REST API 規約に従い `/api/` プレフィックスを使用

### フロントエンド開発

- ナビゲーションには Expo Router を使用（`app/` ディレクトリでファイルベースルーティング）
- NativeWind 経由で TailwindCSS クラスでスタイリング
- バックエンド API 変更後は `npm run generate-types` で型を再生成
- API 型は `types/api.ts` からインポート

### 型安全ワークフロー

1. SpringDoc アノテーション付きでバックエンドコントローラを更新
2. バックエンドを再起動して `openapi.json` を再生成
3. フロントエンドで `npm run generate-types` を実行
4. 更新された TypeScript 型を使用して型安全な API コールを実行

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

## 環境要件

- **Java**: 21 (build.gradle で設定)
- **Node.js**: Expo SDK 53 対応
- **Docker**: PostgreSQL コンテナ用に必要
- **PostgreSQL**: 17 (Docker コンテナ経由)