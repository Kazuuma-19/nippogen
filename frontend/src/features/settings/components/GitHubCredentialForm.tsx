import React, { useState } from 'react'
import { View, Text, TextInput, TouchableOpacity, ScrollView, Alert } from 'react-native'
import { Ionicons } from '@expo/vector-icons'
import { useForm } from '@tanstack/react-form'
import { gitHubCredentialSchema, type GitHubCredentialFormData } from '../schemas/gitHubCredential'
import type { components } from '@/types/api'

type GitHubCredential = components['schemas']['GitHubCredentialResponseDto']

interface GitHubCredentialFormProps {
  initialData?: GitHubCredential | null
  onSave: (data: GitHubCredentialFormData) => Promise<void>
  onCancel: () => void
}

export function GitHubCredentialForm({ 
  initialData, 
  onSave, 
  onCancel 
}: GitHubCredentialFormProps) {
  const [showApiKey, setShowApiKey] = useState(false)
  const [isSaving, setIsSaving] = useState(false)

  const form = useForm({
    defaultValues: {
      apiKey: initialData?.maskedApiKey?.includes('****') ? '' : (initialData?.maskedApiKey || ''),
      baseUrl: initialData?.baseUrl || 'https://api.github.com',
      owner: initialData?.owner || '',
      repo: initialData?.repo || ''
    } as GitHubCredentialFormData,
    
    onSubmit: async ({ value }) => {
      setIsSaving(true)
      try {
        const result = gitHubCredentialSchema.safeParse(value)
        if (!result.success) {
          const firstError = result.error.issues[0]
          Alert.alert('バリデーションエラー', firstError.message)
          return
        }
        
        await onSave(result.data)
      } catch (error) {
        // Error handling is done in the hook
      } finally {
        setIsSaving(false)
      }
    },
    
  })

  return (
    <ScrollView className="flex-1 bg-white">
      <View className="p-6">
        {/* Header */}
        <View className="flex-row items-center justify-between mb-6">
          <Text className="text-xl font-bold text-gray-800">
            GitHub認証情報{initialData ? '編集' : '追加'}
          </Text>
          <TouchableOpacity onPress={onCancel}>
            <Ionicons name="close" size={24} color="gray" />
          </TouchableOpacity>
        </View>

        <View>
          {/* API Key Field */}
          <form.Field name="apiKey">
            {(field) => (
              <View className="mb-4">
                <Text className="text-sm font-medium text-gray-700 mb-2">
                  GitHub API Token <Text className="text-red-500">*</Text>
                </Text>
                <View className="relative">
                  <TextInput
                    value={field.state.value}
                    onChangeText={field.handleChange}
                    onBlur={field.handleBlur}
                    secureTextEntry={!showApiKey}
                    placeholder="ghp_xxxxxxxxxxxxxxxxxxxx"
                    className="border border-gray-300 rounded-lg p-3 pr-12 text-sm"
                    multiline={false}
                  />
                  <TouchableOpacity 
                    onPress={() => setShowApiKey(!showApiKey)}
                    className="absolute right-3 top-3"
                  >
                    <Ionicons 
                      name={showApiKey ? "eye-off" : "eye"} 
                      size={20} 
                      color="gray" 
                    />
                  </TouchableOpacity>
                </View>
                {field.state.meta.errors && field.state.meta.errors.length > 0 && (
                  <Text className="text-red-500 text-xs mt-1">
                    {String(field.state.meta.errors[0])}
                  </Text>
                )}
                <Text className="text-gray-500 text-xs mt-1">
                  GitHub Settings → Developer settings → Personal access tokensで取得
                </Text>
              </View>
            )}
          </form.Field>

          {/* Base URL Field */}
          <form.Field name="baseUrl">
            {(field) => (
              <View className="mb-4">
                <Text className="text-sm font-medium text-gray-700 mb-2">
                  API Base URL
                </Text>
                <TextInput
                  value={field.state.value}
                  onChangeText={field.handleChange}
                  onBlur={field.handleBlur}
                  placeholder="https://api.github.com"
                  className="border border-gray-300 rounded-lg p-3 text-sm"
                  keyboardType="url"
                />
                {field.state.meta.errors && field.state.meta.errors.length > 0 && (
                  <Text className="text-red-500 text-xs mt-1">
                    {String(field.state.meta.errors[0])}
                  </Text>
                )}
                <Text className="text-gray-500 text-xs mt-1">
                  GitHub Enterpriseの場合のみ変更してください
                </Text>
              </View>
            )}
          </form.Field>

          {/* Repository Info */}
          <View className="mb-6">
            <Text className="text-sm font-medium text-gray-700 mb-3">
              リポジトリ情報 <Text className="text-red-500">*</Text>
            </Text>
            
            <View className="flex-row space-x-3">
              {/* Owner Field */}
              <form.Field name="owner">
                {(field) => (
                  <View className="flex-1">
                    <Text className="text-xs text-gray-600 mb-1">Owner</Text>
                    <TextInput
                      value={field.state.value}
                      onChangeText={field.handleChange}
                      onBlur={field.handleBlur}
                      placeholder="octocat"
                      className="border border-gray-300 rounded-lg p-3 text-sm"
                    />
                    {field.state.meta.errors && field.state.meta.errors.length > 0 && (
                      <Text className="text-red-500 text-xs mt-1">
                        {String(field.state.meta.errors[0])}
                      </Text>
                    )}
                  </View>
                )}
              </form.Field>
              
              {/* Repo Field */}
              <form.Field name="repo">
                {(field) => (
                  <View className="flex-1">
                    <Text className="text-xs text-gray-600 mb-1">Repository</Text>
                    <TextInput
                      value={field.state.value}
                      onChangeText={field.handleChange}
                      onBlur={field.handleBlur}
                      placeholder="Hello-World"
                      className="border border-gray-300 rounded-lg p-3 text-sm"
                    />
                    {field.state.meta.errors && field.state.meta.errors.length > 0 && (
                      <Text className="text-red-500 text-xs mt-1">
                        {String(field.state.meta.errors[0])}
                      </Text>
                    )}
                  </View>
                )}
              </form.Field>
            </View>
          </View>

          {/* Info Box */}
          <View className="bg-blue-50 p-4 rounded-lg mb-6">
            <View className="flex-row items-start">
              <Ionicons name="information-circle" size={20} color="#3b82f6" />
              <View className="ml-2 flex-1">
                <Text className="text-blue-800 font-medium text-sm mb-1">
                  必要な権限
                </Text>
                <Text className="text-blue-700 text-xs leading-4">
                  • repo: リポジトリへの読み取りアクセス{'\n'}
                  • user: ユーザー情報の読み取り{'\n'}
                  • read:org: 組織情報の読み取り（該当する場合）
                </Text>
              </View>
            </View>
          </View>

          {/* Action Buttons */}
          <View className="flex-row space-x-3 pt-4">
            <TouchableOpacity
              onPress={onCancel}
              className="flex-1 bg-gray-100 py-3 rounded-lg"
            >
              <Text className="text-gray-700 text-center font-medium">
                キャンセル
              </Text>
            </TouchableOpacity>
            
            <form.Subscribe
              selector={(state) => ({ canSubmit: state.canSubmit, isSubmitting: state.isSubmitting })}
            >
              {({ canSubmit, isSubmitting }) => (
                <TouchableOpacity
                  onPress={form.handleSubmit}
                  disabled={!canSubmit || isSubmitting || isSaving}
                  className={`flex-1 py-3 rounded-lg ${
                    canSubmit && !isSubmitting && !isSaving
                      ? 'bg-primary' 
                      : 'bg-gray-400'
                  }`}
                >
                  <Text className="text-white text-center font-medium">
                    {isSaving ? '保存中...' : initialData ? '更新' : '保存'}
                  </Text>
                </TouchableOpacity>
              )}
            </form.Subscribe>
          </View>
        </View>
      </View>
    </ScrollView>
  )
}