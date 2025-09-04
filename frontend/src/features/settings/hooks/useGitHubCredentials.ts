import { useState, useEffect, useCallback } from 'react'
import { credentialsApi } from '../../../utils/apiClient'
import { showSuccess, showError } from '../../../utils/notification'
import type { components } from '@/types/api'

type GitHubCredential = components['schemas']['GitHubCredentialResponseDto']
type GitHubCredentialCreate = components['schemas']['GitHubCredentialCreateRequestDto']

export function useGitHubCredentials() {
  const [credentials, setCredentials] = useState<GitHubCredential[]>([])
  const [loading, setLoading] = useState(false)
  const [initialLoadComplete, setInitialLoadComplete] = useState(false)

  const loadCredentials = useCallback(async () => {
    setLoading(true)
    try {
      const response = await credentialsApi.github.findAllByUserId()
      setCredentials(response.data)
    } catch {
      showError('GitHub認証情報の読み込みに失敗しました')
    } finally {
      setLoading(false)
    }
  }, [])

  const saveCredential = useCallback(async (data: GitHubCredentialCreate) => {
    try {
      const response = await credentialsApi.github.create(data)
      setCredentials(prev => [...prev, response.data])
      showSuccess('GitHub認証情報を保存しました')
      return response.data
    } catch (error) {
      showError('GitHub認証情報の保存に失敗しました')
      throw error
    }
  }, [])

  const deleteCredential = useCallback(async (id: string) => {
    try {
      await credentialsApi.github.delete(id)
      setCredentials(prev => prev.filter(cred => cred.id !== id))
      showSuccess('GitHub認証情報を削除しました')
    } catch {
      showError('GitHub認証情報の削除に失敗しました')
    }
  }, [])

  const testConnection = useCallback(async (owner: string, repo: string) => {
    try {
      await credentialsApi.github.test(owner, repo)
      showSuccess('GitHub接続テストに成功しました')
    } catch {
      showError('GitHub接続テストに失敗しました')
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