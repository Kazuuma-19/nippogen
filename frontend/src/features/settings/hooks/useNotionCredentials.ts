import { useState, useEffect, useCallback } from 'react'
import { credentialsApi } from '../../../utils/apiClient'
import { showSuccess, showError } from '../../../utils/notification'
import type { components } from '@/types/api'

type NotionCredential = components['schemas']['NotionCredentialResponseDto']
type NotionCredentialCreate = components['schemas']['NotionCredentialCreateRequestDto']

export function useNotionCredentials() {
  const [credentials, setCredentials] = useState<NotionCredential[]>([])
  const [loading, setLoading] = useState(false)
  const [initialLoadComplete, setInitialLoadComplete] = useState(false)

  const loadCredentials = useCallback(async () => {
    setLoading(true)
    try {
      const response = await credentialsApi.notion.findAllByUserId()
      setCredentials(response.data)
    } catch {
      showError('Notion認証情報の読み込みに失敗しました')
    } finally {
      setLoading(false)
    }
  }, [])

  const saveCredential = useCallback(async (data: NotionCredentialCreate) => {
    try {
      const response = await credentialsApi.notion.create(data)
      setCredentials(prev => [...prev, response.data])
      showSuccess('Notion認証情報を保存しました')
      return response.data
    } catch (error) {
      showError('Notion認証情報の保存に失敗しました')
      throw error
    }
  }, [])

  const deleteCredential = useCallback(async (id: string) => {
    try {
      await credentialsApi.notion.delete(id)
      setCredentials(prev => prev.filter(cred => cred.id !== id))
      showSuccess('Notion認証情報を削除しました')
    } catch {
      showError('Notion認証情報の削除に失敗しました')
    }
  }, [])

  const testConnection = useCallback(async () => {
    try {
      await credentialsApi.notion.test()
      showSuccess('Notion接続テストに成功しました')
    } catch {
      showError('Notion接続テストに失敗しました')
    }
  }, [])

  // Initial load
  useEffect(() => {
    if (!initialLoadComplete) {
      loadCredentials().finally(() => setInitialLoadComplete(true))
    }
  }, [loadCredentials, initialLoadComplete])

  return {
    credentials,
    loading,
    loadCredentials,
    saveCredential,
    deleteCredential,
    testConnection,
  }
}