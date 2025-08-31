import { useState, useEffect, useCallback } from 'react'
import { Alert } from 'react-native'
import { credentialsApi } from '../../../utils/apiClient'
import type { components } from '@/types/api'

type TogglCredential = components['schemas']['TogglCredentialResponseDto']
type TogglCredentialCreate = components['schemas']['TogglCredentialCreateRequestDto']

export function useTogglCredentials() {
  const [credentials, setCredentials] = useState<TogglCredential[]>([])
  const [loading, setLoading] = useState(false)
  const [initialLoadComplete, setInitialLoadComplete] = useState(false)

  const loadCredentials = useCallback(async () => {
    setLoading(true)
    try {
      const response = await credentialsApi.toggl.findAllByUserId()
      setCredentials(response.data)
    } catch {
      Alert.alert('エラー', 'Toggl認証情報の読み込みに失敗しました')
    } finally {
      setLoading(false)
    }
  }, [])

  const saveCredential = useCallback(async (data: TogglCredentialCreate) => {
    try {
      const response = await credentialsApi.toggl.create(data)
      setCredentials(prev => [...prev, response.data])
      Alert.alert('成功', 'Toggl認証情報を保存しました')
      return response.data
    } catch (error) {
      Alert.alert('エラー', 'Toggl認証情報の保存に失敗しました')
      throw error
    }
  }, [])

  const deleteCredential = useCallback(async (id: string) => {
    try {
      await credentialsApi.toggl.delete(id)
      setCredentials(prev => prev.filter(cred => cred.id !== id))
      Alert.alert('成功', 'Toggl認証情報を削除しました')
    } catch {
      Alert.alert('エラー', 'Toggl認証情報の削除に失敗しました')
    }
  }, [])

  const testConnection = useCallback(async () => {
    try {
      await credentialsApi.toggl.test()
      Alert.alert('成功', 'Toggl接続テストに成功しました')
    } catch {
      Alert.alert('エラー', 'Toggl接続テストに失敗しました')
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