import React from 'react'
import { View, Text, TouchableOpacity, ScrollView } from 'react-native'
import { Ionicons } from '@expo/vector-icons'

type ServiceType = 'github' | 'toggle' | 'notion'

interface Service {
  key: ServiceType
  name: string
  icon: keyof typeof Ionicons.glyphMap
  color: string
  description: string
}

interface ApiServiceSelectorProps {
  selectedService: ServiceType
  onServiceChange: (service: ServiceType) => void
}

const services: Service[] = [
  {
    key: 'github',
    name: 'GitHub',
    icon: 'logo-github',
    color: 'bg-gray-800',
    description: 'コミットやPRの情報'
  },
  {
    key: 'toggle',
    name: 'Toggle Track',
    icon: 'time',
    color: 'bg-red-500',
    description: '作業時間の記録'
  },
  {
    key: 'notion',
    name: 'Notion',
    icon: 'document-text',
    color: 'bg-gray-700',
    description: 'タスクやメモ'
  }
]

export function ApiServiceSelector({ selectedService, onServiceChange }: ApiServiceSelectorProps) {
  return (
    <View className="mb-6">
      <Text className="text-lg font-semibold text-gray-800 mb-3 px-6">
        連携サービス選択
      </Text>
      
      <ScrollView 
        horizontal 
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={{ paddingHorizontal: 24 }}
      >
        <View className="flex-row space-x-3">
          {services.map((service) => {
            const isSelected = selectedService === service.key
            
            return (
              <TouchableOpacity
                key={service.key}
                onPress={() => onServiceChange(service.key)}
                className={`px-4 py-4 rounded-xl min-w-[120px] items-center ${
                  isSelected 
                    ? `${service.color} shadow-lg` 
                    : 'bg-white border-2 border-gray-100'
                }`}
              >
                <Ionicons 
                  name={service.icon} 
                  size={28} 
                  color={isSelected ? 'white' : '#6b7280'} 
                />
                
                <Text className={`font-semibold mt-2 text-center ${
                  isSelected ? 'text-white' : 'text-gray-700'
                }`}>
                  {service.name}
                </Text>
                
                <Text className={`text-xs mt-1 text-center ${
                  isSelected ? 'text-gray-200' : 'text-gray-500'
                }`}>
                  {service.description}
                </Text>
              </TouchableOpacity>
            )
          })}
        </View>
      </ScrollView>
      
      <View className="mt-4 mx-6 bg-blue-50 p-3 rounded-lg">
        <Text className="text-blue-800 text-sm">
          💡 各サービスで複数の認証情報を設定できます
        </Text>
      </View>
    </View>
  )
}

export type { ServiceType }