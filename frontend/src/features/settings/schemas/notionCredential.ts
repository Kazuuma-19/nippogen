import { z } from 'zod'

export const notionCredentialSchema = z.object({
  apiKey: z
    .string()
    .min(1, 'APIキーは必須です')
    .startsWith('secret_', 'Notion APIキーはsecret_で始まる必要があります')
    .min(57, 'Notion APIキーは57文字以上である必要があります'),
  
  databaseId: z
    .string()
    .optional()
    .refine(
      (val) => !val || /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(val.replace(/-/g, '')),
      { message: 'データベースIDの形式が正しくありません' }
    ),
  
  titleProperty: z
    .string()
    .optional()
    .default('Name'),
  
  statusProperty: z
    .string()
    .optional()
    .default('Status'),
  
  dateProperty: z
    .string()
    .optional()
    .default('Date'),
  
  filterConditions: z
    .record(z.string(), z.unknown())
    .optional()
    .default({})
})

export type NotionCredentialFormData = z.infer<typeof notionCredentialSchema>