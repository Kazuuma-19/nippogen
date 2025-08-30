import { z } from "zod";

export const toggleCredentialSchema = z.object({
  apiKey: z
    .string()
    .min(1, "APIキーは必須です")
    .min(32, "Toggle APIキーは32文字以上である必要があります"),

  workspaceId: z
    .number()
    .int("ワークスペースIDは整数である必要があります")
    .positive("ワークスペースIDは正の数である必要があります")
    .optional(),

  projectIds: z.array(z.number().int().positive()).optional().default([]),

  defaultTags: z.array(z.string().min(1)).optional().default([]),

  timeZone: z.string().optional().default("UTC"),

  includeWeekends: z.boolean().optional().default(false),
});

export type ToggleCredentialFormData = z.infer<typeof toggleCredentialSchema>;

// バリデーションエラーメッセージの日本語化
export const toggleCredentialErrorMessages = {
  apiKey: {
    required: "APIキーは必須です",
    tooShort: "Toggle APIキーは32文字以上である必要があります",
  },
  workspaceId: {
    invalid: "ワークスペースIDは正の整数である必要があります",
  },
  projectIds: {
    invalid: "プロジェクトIDは正の整数である必要があります",
  },
  defaultTags: {
    empty: "タグは空にできません",
  },
  timeZone: {
    invalid: "有効なタイムゾーンを入力してください",
  },
};
