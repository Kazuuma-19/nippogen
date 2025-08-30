import React, { useState } from 'react'
import { View, Text, ScrollView } from 'react-native'
import { ApiServiceSelector, type ServiceType } from '../../src/features/settings/components/ApiServiceSelector'
import { GitHubCredentialSection } from '../../src/features/settings/components/GitHubCredentialSection'
import { TogglCredentialSection } from '../../src/features/settings/components/TogglCredentialSection'
import { NotionCredentialSection } from '../../src/features/settings/components/NotionCredentialSection'

export default function ApiCredentialsScreen() {
  const [selectedService, setSelectedService] = useState<ServiceType>('github')

  const renderServiceSection = () => {
    switch (selectedService) {
      case 'github':
        return <GitHubCredentialSection />
      case 'toggl':
        return <TogglCredentialSection />
      case 'notion':
        return <NotionCredentialSection />
      default:
        return null
    }
  }

  return (
    <View className="flex-1 bg-gray-50">
      <ScrollView showsVerticalScrollIndicator={false}>
        <View className="pt-6">
          {/* Header */}
          <View className="px-6 mb-4">
            <Text className="text-2xl font-bold text-gray-800 mb-2">
              API認証情報
            </Text>
            <Text className="text-gray-600 text-sm">
              外部サービスとの連携に必要な認証情報を管理します
            </Text>
          </View>

          {/* Service Selector */}
          <ApiServiceSelector
            selectedService={selectedService}
            onServiceChange={setSelectedService}
          />

          {/* Service Section */}
          <View className="bg-white mx-6 rounded-xl shadow-sm">
            {renderServiceSection()}
          </View>

          {/* Bottom spacing */}
          <View className="h-8" />
        </View>
      </ScrollView>
    </View>
  )
}