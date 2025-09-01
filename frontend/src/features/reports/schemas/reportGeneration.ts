import { z } from 'zod'

export const reportGenerationSchema = z.object({
  selectedDate: z
    .string()
    .min(1, '日付は必須です')
    .regex(/^\d{4}-\d{2}-\d{2}$/, '正しい日付形式で入力してください (YYYY-MM-DD)'),
  
  additionalNotes: z
    .string()
    .optional()
})

export type ReportGenerationFormData = z.infer<typeof reportGenerationSchema>