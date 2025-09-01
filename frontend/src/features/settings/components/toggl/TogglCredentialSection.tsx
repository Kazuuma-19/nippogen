import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Modal,
  ActivityIndicator,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { CredentialCard } from "../CredentialCard";
import { TogglCredentialForm } from "./TogglCredentialForm";
import { useTogglCredentials } from "../../hooks/useTogglCredentials";
import type { components } from "@/types/api";
import type { TogglCredentialFormData } from "../../schemas/togglCredential";

type TogglCredential = components["schemas"]["TogglCredentialResponseDto"];

export function TogglCredentialSection() {
  const {
    credentials,
    loading,
    saveCredential,
    deleteCredential,
    testConnection,
  } = useTogglCredentials();

  const [isFormVisible, setIsFormVisible] = useState(false);
  const [editingCredential, setEditingCredential] =
    useState<TogglCredential | null>(null);

  const togglCredentials = credentials;

  const handleEdit = (credential: TogglCredential) => {
    setEditingCredential(credential);
    setIsFormVisible(true);
  };

  const handleDelete = async (credential: TogglCredential) => {
    if (credential.id) {
      await deleteCredential(credential.id);
    }
  };

  const handleTest = async (credential: TogglCredential) => {
    await testConnection();
  };

  const handleSave = async (data: TogglCredentialFormData) => {
    // For now, only support creating new credentials
    await saveCredential(data);

    setIsFormVisible(false);
    setEditingCredential(null);
  };

  const handleCancel = () => {
    setIsFormVisible(false);
    setEditingCredential(null);
  };

  return (
    <View className="p-6">
      {/* Header */}
      <View className="flex-row items-center justify-between mb-4">
        <View className="flex-row items-center">
          <Ionicons name="time" size={24} color="#ef4444" />
          <Text className="text-xl font-bold text-gray-800 ml-2">
            Toggl Track認証情報
          </Text>
        </View>

        <TouchableOpacity
          onPress={() => setIsFormVisible(true)}
          className="bg-primary px-4 py-2 rounded-lg flex-row items-center space-x-2"
        >
          <Ionicons name="add" size={20} color="white" />
          <Text className="text-white font-medium">追加</Text>
        </TouchableOpacity>
      </View>

      {/* Loading State */}
      {loading && (
        <View className="items-center py-8">
          <ActivityIndicator size="large" color="#267D00" />
          <Text className="text-gray-500 mt-2">読み込み中...</Text>
        </View>
      )}

      {/* Credentials List */}
      {!loading && (
        <>
          {togglCredentials.length > 0 ? (
            <View>
              {togglCredentials.map((credential) => (
                <CredentialCard
                  key={credential.id}
                  credential={credential}
                  serviceType="toggl"
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                  onTest={handleTest}
                />
              ))}
            </View>
          ) : (
            <View className="bg-gray-50 p-8 rounded-xl items-center">
              <Ionicons name="time" size={48} color="gray" />
              <Text className="text-gray-500 mt-4 text-center font-medium">
                Toggl Track認証情報が設定されていません
              </Text>
              <Text className="text-gray-400 mt-2 text-center text-sm">
                作業時間の記録を取得するために{"\n"}
                Toggl Track APIの認証情報を追加してください
              </Text>

              <TouchableOpacity
                onPress={() => setIsFormVisible(true)}
                className="mt-4 bg-primary px-6 py-2 rounded-lg"
              >
                <Text className="text-white font-medium">
                  最初の認証情報を追加
                </Text>
              </TouchableOpacity>
            </View>
          )}
        </>
      )}

      {/* Usage Info */}
      <View className="mt-6 bg-red-50 p-4 rounded-lg">
        <View className="flex-row items-start">
          <Ionicons name="bulb" size={20} color="#ef4444" />
          <View className="ml-2 flex-1">
            <Text className="text-red-800 font-medium text-sm mb-1">
              Toggl Track認証情報について
            </Text>
            <Text className="text-red-700 text-xs leading-4">
              • 複数のワークスペースやプロジェクトを管理できます{"\n"}• API
              Tokenを使用して安全に作業時間データを取得します{"\n"}•
              日報生成時に指定した期間の作業時間を自動集計します{"\n"}•
              プロジェクト別、タグ別の時間配分も表示されます
            </Text>
          </View>
        </View>
      </View>

      {/* Form Modal */}
      <Modal
        visible={isFormVisible}
        animationType="slide"
        presentationStyle="pageSheet"
      >
        <TogglCredentialForm
          initialData={editingCredential}
          onSave={handleSave}
          onCancel={handleCancel}
        />
      </Modal>
    </View>
  );
}
