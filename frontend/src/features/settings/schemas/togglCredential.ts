import { z } from 'zod'

export const togglCredentialSchema = z.object({
  apiKey: z
    .string()
    .min(1, 'APIキーは必須です')
    .min(32, 'Toggl APIキーは32文字以上である必要があります'),
  
  workspaceId: z
    .number()
    .int('ワークスペースIDは整数である必要があります')
    .positive('ワークスペースIDは正の数である必要があります')
    .optional(),
  
  projectIds: z
    .array(z.number().int().positive())
    .optional()
    .default([]),
  
  defaultTags: z
    .array(z.string().min(1))
    .optional()
    .default([]),
  
  timeZone: z
    .string()
    .optional()
    .default('UTC'),
  
  includeWeekends: z
    .boolean()
    .optional()
    .default(false)
})

export type TogglCredentialFormData = z.infer<typeof togglCredentialSchema>