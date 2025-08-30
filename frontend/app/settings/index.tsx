import React from 'react'
import { View, Text, TouchableOpacity, ScrollView } from 'react-native'
import { Ionicons } from '@expo/vector-icons'
import { router } from 'expo-router'

export default function SettingsScreen() {
  const settingsItems = [
    {
      id: 'api-credentials',
      title: 'API認証情報',
      description: 'GitHub、Toggl、Notionの認証情報を管理',
      icon: 'key' as keyof typeof Ionicons.glyphMap,
      color: 'bg-blue-500',
      route: '/settings/api-credentials'
    },
    {
      id: 'notification',
      title: '通知設定',
      description: '日報生成完了やエラー通知の設定',
      icon: 'notifications' as keyof typeof Ionicons.glyphMap,
      color: 'bg-orange-500',
      route: null // 未実装
    },
    {
      id: 'export',
      title: 'エクスポート設定',
      description: '日報の出力形式やテンプレートの設定',
      icon: 'download' as keyof typeof Ionicons.glyphMap,
      color: 'bg-green-500',
      route: null // 未実装
    },
    {
      id: 'account',
      title: 'アカウント設定',
      description: 'ユーザー情報やプライバシー設定',
      icon: 'person' as keyof typeof Ionicons.glyphMap,
      color: 'bg-purple-500',
      route: null // 未実装
    }
  ]

  const handleItemPress = (item: typeof settingsItems[0]) => {
    if (item.route) {
      router.push(item.route as any)
    } else {
      // 未実装の場合のアラート等
      console.log(`${item.title} は未実装です`)
    }
  }

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <View className="p-6">
        {/* Header */}
        <View className="mb-6">
          <Text className="text-2xl font-bold text-gray-800 mb-2">
            設定
          </Text>
          <Text className="text-gray-600 text-sm">
            Nippogenの動作をカスタマイズできます
          </Text>
        </View>

        {/* Settings Items */}
        <View className="space-y-3">
          {settingsItems.map((item) => (
            <TouchableOpacity
              key={item.id}
              onPress={() => handleItemPress(item)}
              className={`bg-white p-4 rounded-xl shadow-sm border border-gray-100 ${
                !item.route ? 'opacity-60' : ''
              }`}
              disabled={!item.route}
            >
              <View className="flex-row items-center">
                <View className={`w-12 h-12 ${item.color} rounded-full items-center justify-center mr-4`}>
                  <Ionicons name={item.icon} size={24} color="white" />
                </View>
                
                <View className="flex-1">
                  <Text className="font-semibold text-gray-800 text-base">
                    {item.title}
                  </Text>
                  <Text className="text-gray-500 text-sm mt-1">
                    {item.description}
                  </Text>
                </View>

                <View className="ml-2">
                  {item.route ? (
                    <Ionicons name="chevron-forward" size={20} color="#9ca3af" />
                  ) : (
                    <View className="bg-gray-200 px-2 py-1 rounded">
                      <Text className="text-xs text-gray-600">未実装</Text>
                    </View>
                  )}
                </View>
              </View>
            </TouchableOpacity>
          ))}
        </View>

        {/* App Info */}
        <View className="mt-8 bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <Text className="font-semibold text-gray-800 mb-2">
            アプリについて
          </Text>
          <Text className="text-gray-600 text-sm">
            Nippogen v1.0.0{'\n'}
            AI駆動の日報生成アプリケーション
          </Text>
        </View>
      </View>
    </ScrollView>
  )
}