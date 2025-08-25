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