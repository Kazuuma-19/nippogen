import { useState, useEffect, useCallback } from 'react'
import { Alert } from 'react-native'
import { credentialsApi } from '../../../utils/apiClient'
import type { components } from '@/types/api'

type GitHubCredential = components['schemas']['GitHubCredentialResponseDto']
type ToggleeCredential = components['schemas']['ToggleeCredentialResponseDto']  
type NotionCredential = components['schemas']['NotionCredentialResponseDto']

type GitHubCredentialCreate = components['schemas']['GitHubCredentialCreateRequestDto']
type ToggleeCredentialCreate = components['schemas']['ToggleeCredentialCreateRequestDto']
type NotionCredentialCreate = components['schemas']['NotionCredentialCreateRequestDto']

interface CredentialState {
  github: GitHubCredential[]
  togglee: ToggleeCredential[]
  notion: NotionCredential[]
}

interface LoadingState {
  github: boolean
  togglee: boolean
  notion: boolean
}

export function useApiCredentials() {
  const [credentials, setCredentials] = useState<CredentialState>({
    github: [],
    togglee: [],
    notion: []
  })
  
  const [loading, setLoading] = useState<LoadingState>({
    github: false,
    togglee: false,
    notion: false
  })
  
  const [initialLoadComplete, setInitialLoadComplete] = useState(false)

  // GitHub credentials management
  const loadGitHubCredentials = useCallback(async () => {
    setLoading(prev => ({ ...prev, github: true }))
    try {
      const response = await credentialsApi.github.findAllByUserId()
      setCredentials(prev => ({ ...prev, github: response.data }))
    } catch (error) {
      console.error('Failed to load GitHub credentials:', error)
      Alert.alert('エラー', 'GitHub認証情報の読み込みに失敗しました')
    } finally {
      setLoading(prev => ({ ...prev, github: false }))
    }
  }, [])

  const saveGitHubCredential = useCallback(async (data: GitHubCredentialCreate) => {
    try {
      const response = await credentialsApi.github.create(data)
      setCredentials(prev => ({ 
        ...prev, 
        github: [...prev.github, response.data] 
      }))
      Alert.alert('成功', 'GitHub認証情報を保存しました')
      return response.data
    } catch (error) {
      console.error('Failed to save GitHub credential:', error)
      Alert.alert('エラー', 'GitHub認証情報の保存に失敗しました')
      throw error
    }
  }, [])

  const updateGitHubCredential = useCallback(async (id: string, data: components['schemas']['GitHubCredentialUpdateRequestDto']) => {
    try {
      const response = await credentialsApi.github.update(id, data)
      setCredentials(prev => ({
        ...prev,
        github: prev.github.map(cred => 
          cred.id === id ? response.data : cred
        )
      }))
      Alert.alert('成功', 'GitHub認証情報を更新しました')
      return response.data
    } catch (error) {
      console.error('Failed to update GitHub credential:', error)
      Alert.alert('エラー', 'GitHub認証情報の更新に失敗しました')
      throw error
    }
  }, [])

  const deleteGitHubCredential = useCallback(async (id: string) => {
    try {
      await credentialsApi.github.delete(id)
      setCredentials(prev => ({
        ...prev,
        github: prev.github.filter(cred => cred.id !== id)
      }))
      Alert.alert('成功', 'GitHub認証情報を削除しました')
    } catch (error) {
      console.error('Failed to delete GitHub credential:', error)
      Alert.alert('エラー', 'GitHub認証情報の削除に失敗しました')
      throw error
    }
  }, [])

  const testGitHubConnection = useCallback(async (owner: string, repo: string) => {
    try {
      await credentialsApi.github.test(owner, repo)
      return true
    } catch (error) {
      console.error('GitHub connection test failed:', error)
      throw error
    }
  }, [])

  // Toggle credentials management
  const loadToggleCredentials = useCallback(async () => {
    setLoading(prev => ({ ...prev, toggle: true }))
    try {
      const response = await credentialsApi.toggle.findAllByUserId()
      setCredentials(prev => ({ ...prev, toggle: response.data }))
    } catch (error) {
      console.error('Failed to load Toggle credentials:', error)
      Alert.alert('エラー', 'Toggle認証情報の読み込みに失敗しました')
    } finally {
      setLoading(prev => ({ ...prev, toggle: false }))
    }
  }, [])

  const saveToggleCredential = useCallback(async (data: ToggleCredentialCreate) => {
    try {
      const response = await credentialsApi.toggle.create(data)
      setCredentials(prev => ({ 
        ...prev, 
        toggle: [...prev.toggle, response.data] 
      }))
      Alert.alert('成功', 'Toggle認証情報を保存しました')
      return response.data
    } catch (error) {
      console.error('Failed to save Toggle credential:', error)
      Alert.alert('エラー', 'Toggle認証情報の保存に失敗しました')
      throw error
    }
  }, [])

  const deleteToggleCredential = useCallback(async (id: string) => {
    try {
      await credentialsApi.toggle.delete(id)
      setCredentials(prev => ({
        ...prev,
        toggle: prev.toggle.filter(cred => cred.id !== id)
      }))
      Alert.alert('成功', 'Toggle認証情報を削除しました')
    } catch (error) {
      console.error('Failed to delete Toggle credential:', error)
      Alert.alert('エラー', 'Toggle認証情報の削除に失敗しました')
      throw error
    }
  }, [])

  const testToggleConnection = useCallback(async () => {
    try {
      await credentialsApi.toggle.test()
      return true
    } catch (error) {
      console.error('Toggle connection test failed:', error)
      throw error
    }
  }, [])

  // Notion credentials management
  const loadNotionCredentials = useCallback(async () => {
    setLoading(prev => ({ ...prev, notion: true }))
    try {
      const response = await credentialsApi.notion.findAllByUserId()
      setCredentials(prev => ({ ...prev, notion: response.data }))
    } catch (error) {
      console.error('Failed to load Notion credentials:', error)
      Alert.alert('エラー', 'Notion認証情報の読み込みに失敗しました')
    } finally {
      setLoading(prev => ({ ...prev, notion: false }))
    }
  }, [])

  const saveNotionCredential = useCallback(async (data: NotionCredentialCreate) => {
    try {
      const response = await credentialsApi.notion.create(data)
      setCredentials(prev => ({ 
        ...prev, 
        notion: [...prev.notion, response.data] 
      }))
      Alert.alert('成功', 'Notion認証情報を保存しました')
      return response.data
    } catch (error) {
      console.error('Failed to save Notion credential:', error)
      Alert.alert('エラー', 'Notion認証情報の保存に失敗しました')
      throw error
    }
  }, [])

  const deleteNotionCredential = useCallback(async (id: string) => {
    try {
      await credentialsApi.notion.delete(id)
      setCredentials(prev => ({
        ...prev,
        notion: prev.notion.filter(cred => cred.id !== id)
      }))
      Alert.alert('成功', 'Notion認証情報を削除しました')
    } catch (error) {
      console.error('Failed to delete Notion credential:', error)
      Alert.alert('エラー', 'Notion認証情報の削除に失敗しました')
      throw error
    }
  }, [])

  const testNotionConnection = useCallback(async () => {
    try {
      await credentialsApi.notion.test()
      return true
    } catch (error) {
      console.error('Notion connection test failed:', error)
      throw error
    }
  }, [])

  // Load all credentials on mount
  const loadAllCredentials = useCallback(async () => {
    await Promise.all([
      loadGitHubCredentials(),
      loadToggleCredentials(),
      loadNotionCredentials()
    ])
    setInitialLoadComplete(true)
  }, [loadGitHubCredentials, loadToggleCredentials, loadNotionCredentials])

  useEffect(() => {
    loadAllCredentials()
  }, [loadAllCredentials])

  return {
    // State
    credentials,
    loading,
    initialLoadComplete,
    
    // GitHub
    loadGitHubCredentials,
    saveGitHubCredential,
    updateGitHubCredential,
    deleteGitHubCredential,
    testGitHubConnection,
    
    // Toggle
    loadToggleCredentials,
    saveToggleCredential,
    deleteToggleCredential,
    testToggleConnection,
    
    // Notion
    loadNotionCredentials,
    saveNotionCredential,
    deleteNotionCredential,
    testNotionConnection,
    
    // General
    loadAllCredentials
  }
}