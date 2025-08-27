# PR作成ガイド

このコマンドは、Nippogenプロジェクトの開発ガイドラインに基づいてプルリクエストを作成する手順を提供します。

## プルリクエスト作成手順

### 1. 前提条件チェック

- [ ] フィーチャーブランチで作業していることを確認
- [ ] 細かいコミットで変更を記録済み
- [ ] 関連Issueが存在する（作成済み）

### 2. プッシュとPR作成

```bash
# リモートブランチにプッシュ
git push -u origin feature/branch-name

# GitHub CLI でPR作成
gh pr create --title "日本語タイトル" --body "詳細説明"
```

**重要**: PRを作成する際は、`.github/pull_request_template.md` のテンプレートを**必須使用**してください。

### 3. PR必須内容

PRには必ず以下を含める：

#### **PR に関連する issue**
- 関連issueへの参照（例：`close #123`、`fixes #456`）

#### **やったこと**
- 日本語での変更の明確な説明
- このPR内でやったことを記載

#### **レビュー情報**
- このPR内で特にレビューしてほしいところ
- レビューをする上で参考になる情報を記載

#### **実装の思考プロセス**（詳細な背景情報として）
- なぜこのアプローチを選択したか
- 他の選択肢と比較検討した内容
- 実装時に考慮した点や制約
- 将来の拡張性や保守性への配慮

#### **テスト手順**
```
1. バックエンドビルド・起動:
   cd backend && ./gradlew build && ./gradlew bootRun

2. API確認:
   - http://localhost:8080/swagger-ui.html

3. フロントエンド確認:
   cd frontend && npm run start
```

#### **影響範囲**
- 変更の範囲と潜在的影響
- 既存機能への影響
- データベース変更の有無

#### **スクリーンショット**（UI変更時）
- UI変更のスクリーンショット/録画

## コミットメッセージ形式

```
type(scope): 日本語での説明

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### typeの種類
- `feat`: 新機能
- `fix`: バグ修正
- `docs`: ドキュメント変更
- `refactor`: リファクタリング
- `test`: テスト追加
- `chore`: 保守作業

### 例
```
feat(api): ユーザー認証エンドポイントを追加
fix(frontend): ログインページのナビゲーション問題を修正
docs(readme): インストール手順を更新
```

## レビュープロセス

1. @Kazuuma-19 にレビューを要求
2. **自分でPRをマージしない**
3. レビューフィードバックに対応
4. 再レビュー要求前に全コメントに対応
5. 承認後のマージを待つ

## ブランチ命名規則

**形式**: `feature/issue-number-short-description`

**例**:
- `feature/6-database-migration`
- `feature/10-ai-report-generation` 
- `feature/14-api-settings-screen`

## 注意事項

- PR タイトルと説明は**日本語**で記述
- 全 PR は @Kazuuma-19 によるレビュー必須
- レビュー承認を待ってからマージ
- 細かいコミットを推奨（rebaseは避ける）