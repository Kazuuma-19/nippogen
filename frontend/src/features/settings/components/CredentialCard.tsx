import React, { useState } from 'react'
import { View, Text, TouchableOpacity, ActivityIndicator, Alert } from 'react-native'
import { Ionicons } from '@expo/vector-icons'
import type { components } from '@/types/api'

type GitHubCredential = components['schemas']['GitHubCredentialResponseDto']
type TogglCredential = components['schemas']['TogglCredentialResponseDto']  
type NotionCredential = components['schemas']['NotionCredentialResponseDto']

type CredentialType = GitHubCredential | TogglCredential | NotionCredential

interface CredentialCardProps {
  credential: CredentialType
  serviceType: 'github' | 'toggl' | 'notion'
  onEdit: (credential: CredentialType) => void
  onDelete: (credential: CredentialType) => void
  onTest: (credential: CredentialType) => Promise<void>
}

export function CredentialCard({ 
  credential, 
  serviceType, 
  onEdit, 
  onDelete, 
  onTest 
}: CredentialCardProps) {
  const [isExpanded, setIsExpanded] = useState(false)
  const [isTestLoading, setIsTestLoading] = useState(false)

  const handleTest = async () => {
    setIsTestLoading(true)
    try {
      await onTest(credential)
      Alert.alert('成功', '接続テストに成功しました')
    } catch {
      Alert.alert('エラー', '接続テストに失敗しました')
    } finally {
      setIsTestLoading(false)
    }
  }

  const handleDelete = () => {
    Alert.alert(
      '削除確認',
      'この認証情報を削除しますか？',
      [
        { text: 'キャンセル', style: 'cancel' },
        { 
          text: '削除', 
          style: 'destructive',
          onPress: () => onDelete(credential)
        }
      ]
    )
  }

  const getDisplayName = () => {
    switch (serviceType) {
      case 'github':
        const githubCred = credential as GitHubCredential
        return githubCred.fullRepoName || `${githubCred.owner}/${githubCred.repo}` || 'GitHub認証情報'
      case 'toggl':
        const togglCred = credential as TogglCredential
        return `Workspace ID: ${togglCred.workspaceId || 'N/A'}`
      case 'notion':
        const notionCred = credential as NotionCredential
        return notionCred.databaseId ? `Database: ${notionCred.databaseId.slice(0, 8)}...` : 'Notion認証情報'
      default:
        return 'API認証情報'
    }
  }

  const getSubInfo = () => {
    switch (serviceType) {
      case 'github':
        const githubCred = credential as GitHubCredential
        return githubCred.baseUrl || 'https://api.github.com'
      case 'toggl':
        const togglCred = credential as TogglCredential
        return `${togglCred.projectCount || 0}個のプロジェクト`
      case 'notion':
        const notionCred = credential as NotionCredential
        return notionCred.fullyConfigured ? '設定完了' : '設定が不完全'
      default:
        return ''
    }
  }

  const getStatusColor = () => {
    if (!credential.active) return 'bg-gray-400'
    
    switch (serviceType) {
      case 'github':
        return 'bg-gray-800'
      case 'toggl':
        return 'bg-red-500'
      case 'notion':
        const notionCred = credential as NotionCredential
        return notionCred.fullyConfigured ? 'bg-gray-700' : 'bg-yellow-500'
      default:
        return 'bg-gray-400'
    }
  }

  return (
    <View className="bg-white rounded-xl p-4 mb-3 shadow-sm border border-gray-100">
      {/* Header */}
      <View className="flex-row items-center justify-between">
        <View className="flex-1 mr-3">
          <View className="flex-row items-center mb-1">
            <View className={`w-3 h-3 rounded-full mr-2 ${getStatusColor()}`} />
            <Text className="font-semibold text-gray-800 flex-1" numberOfLines={1}>
              {getDisplayName()}
            </Text>
          </View>
          
          <Text className="text-sm text-gray-500 mb-1">
            {credential.maskedApiKey || '****'}
          </Text>
          
          <Text className="text-xs text-gray-400">
            {getSubInfo()}
          </Text>
        </View>
        
        <View className="flex-row items-center space-x-2">
          <TouchableOpacity
            onPress={handleTest}
            disabled={isTestLoading}
            className="bg-blue-500 px-3 py-2 rounded-lg min-w-[44px] items-center"
          >
            {isTestLoading ? (
              <ActivityIndicator size="small" color="white" />
            ) : (
              <Ionicons name="checkmark-circle" size={16} color="white" />
            )}
          </TouchableOpacity>
          
          <TouchableOpacity
            onPress={() => setIsExpanded(!isExpanded)}
            className="bg-gray-100 px-3 py-2 rounded-lg"
          >
            <Ionicons 
              name={isExpanded ? "chevron-up" : "chevron-down"} 
              size={16} 
              color="gray" 
            />
          </TouchableOpacity>
        </View>
      </View>

      {/* Status indicator */}
      {!credential.active && (
        <View className="mt-2 bg-gray-50 px-2 py-1 rounded">
          <Text className="text-xs text-gray-600">非アクティブ</Text>
        </View>
      )}
      
      {/* Expanded Content */}
      {isExpanded && (
        <View className="mt-4 pt-4 border-t border-gray-100">
          <View className="mb-3">
            <Text className="text-xs text-gray-500 mb-1">作成日時</Text>
            <Text className="text-sm text-gray-700">
              {credential.createdAt ? new Date(credential.createdAt).toLocaleDateString('ja-JP') : 'N/A'}
            </Text>
          </View>
          
          <View className="flex-row space-x-2">
            <TouchableOpacity
              onPress={() => onEdit(credential)}
              className="flex-1 bg-primary py-2 rounded-lg"
            >
              <Text className="text-white text-center font-medium">編集</Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              onPress={handleDelete}
              className="flex-1 bg-red-500 py-2 rounded-lg"
            >
              <Text className="text-white text-center font-medium">削除</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}
    </View>
  )
}