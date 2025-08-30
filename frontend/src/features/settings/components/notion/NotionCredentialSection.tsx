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
import { NotionCredentialForm } from "./NotionCredentialForm";
import { useNotionCredentials } from "../../hooks/useNotionCredentials";
import type { components } from "@/types/api";
import type { NotionCredentialFormData } from "../../schemas/notionCredential";

type NotionCredential = components["schemas"]["NotionCredentialResponseDto"];

export function NotionCredentialSection() {
  const {
    credentials,
    loading,
    saveCredential,
    deleteCredential,
    testConnection,
  } = useNotionCredentials();

  const [isFormVisible, setIsFormVisible] = useState(false);
  const [editingCredential, setEditingCredential] =
    useState<NotionCredential | null>(null);

  const notionCredentials = credentials;

  const handleEdit = (credential: NotionCredential) => {
    setEditingCredential(credential);
    setIsFormVisible(true);
  };

  const handleDelete = async (credential: NotionCredential) => {
    if (credential.id) {
      await deleteCredential(credential.id);
    }
  };

  const handleTest = async (credential: NotionCredential) => {
    await testConnection();
  };

  const handleSave = async (data: NotionCredentialFormData) => {
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
          <Ionicons name="document-text" size={24} color="#374151" />
          <Text className="text-xl font-bold text-gray-800 ml-2">
            Notion認証情報
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
      {loading.notion && (
        <View className="items-center py-8">
          <ActivityIndicator size="large" color="#267D00" />
          <Text className="text-gray-500 mt-2">読み込み中...</Text>
        </View>
      )}

      {/* Credentials List */}
      {!loading.notion && (
        <>
          {notionCredentials.length > 0 ? (
            <View>
              {notionCredentials.map((credential) => (
                <CredentialCard
                  key={credential.id}
                  credential={credential}
                  serviceType="notion"
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                  onTest={handleTest}
                />
              ))}
            </View>
          ) : (
            <View className="bg-gray-50 p-8 rounded-xl items-center">
              <Ionicons name="document-text" size={48} color="gray" />
              <Text className="text-gray-500 mt-4 text-center font-medium">
                Notion認証情報が設定されていません
              </Text>
              <Text className="text-gray-400 mt-2 text-center text-sm">
                タスクやメモの情報を取得するために{"\n"}
                Notion APIの認証情報を追加してください
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
      <View className="mt-6 bg-gray-50 p-4 rounded-lg">
        <View className="flex-row items-start">
          <Ionicons name="bulb" size={20} color="#374151" />
          <View className="ml-2 flex-1">
            <Text className="text-gray-800 font-medium text-sm mb-1">
              Notion認証情報について
            </Text>
            <Text className="text-gray-700 text-xs leading-4">
              • 複数のデータベースやワークスペースを管理できます{"\n"}•
              Integration Tokenを使用して安全にデータを取得します{"\n"}•
              日報生成時にタスクの進捗状況やメモを自動取得します{"\n"}•
              プロパティ名を設定することで柔軟にデータを抽出できます
            </Text>
          </View>
        </View>
      </View>

      {/* Setup Guide */}
      <View className="mt-4 bg-blue-50 p-4 rounded-lg">
        <View className="flex-row items-start">
          <Ionicons name="help-circle" size={20} color="#3b82f6" />
          <View className="ml-2 flex-1">
            <Text className="text-blue-800 font-medium text-sm mb-1">
              セットアップガイド
            </Text>
            <Text className="text-blue-700 text-xs leading-4">
              1. notion.so/my-integrations でIntegrationを作成{"\n"}
              2. 使用するデータベースにIntegrationを接続{"\n"}
              3. データベースURLからIDを取得{"\n"}
              4. プロパティ名を確認して正確に入力
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
        <NotionCredentialForm
          initialData={editingCredential}
          onSave={handleSave}
          onCancel={handleCancel}
        />
      </Modal>
    </View>
  );
}
