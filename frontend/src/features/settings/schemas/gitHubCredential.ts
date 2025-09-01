import { z } from 'zod'

export const gitHubCredentialSchema = z.object({
  apiKey: z
    .string()
    .min(1, 'APIキーは必須です')
    .regex(/^gh[ps]_[A-Za-z0-9]{36}$/, 'GitHub APIキーの形式が正しくありません'),
  
  baseUrl: z
    .url('有効なURLを入力してください')
    .optional()
    .default('https://api.github.com'),
  
  owner: z
    .string()
    .min(1, 'オーナー名は必須です')
    .regex(/^[a-zA-Z0-9-]+$/, 'オーナー名は英数字とハイフンのみ使用できます')
    .max(39, 'オーナー名は39文字以内で入力してください'),
  
  repo: z
    .string()
    .min(1, 'リポジトリ名は必須です')
    .regex(/^[a-zA-Z0-9-_.]+$/, 'リポジトリ名は英数字、ハイフン、アンダースコア、ドットのみ使用できます')
    .max(100, 'リポジトリ名は100文字以内で入力してください')
})

export type GitHubCredentialFormData = z.infer<typeof gitHubCredentialSchema>