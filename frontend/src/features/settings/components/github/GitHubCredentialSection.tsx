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
import { GitHubCredentialForm } from "./GitHubCredentialForm";
import { useApiCredentials } from "../../hooks/useApiCredentials";
import type { components } from "@/types/api";
import type { GitHubCredentialFormData } from "../../schemas/gitHubCredential";

type GitHubCredential = components["schemas"]["GitHubCredentialResponseDto"];

export function GitHubCredentialSection() {
  const {
    credentials,
    loading,
    saveGitHubCredential,
    updateGitHubCredential,
    deleteGitHubCredential,
    testGitHubConnection,
  } = useApiCredentials();

  const [isFormVisible, setIsFormVisible] = useState(false);
  const [editingCredential, setEditingCredential] =
    useState<GitHubCredential | null>(null);

  const gitHubCredentials = credentials.github;

  const handleEdit = (credential: GitHubCredential) => {
    setEditingCredential(credential);
    setIsFormVisible(true);
  };

  const handleDelete = async (credential: GitHubCredential) => {
    if (credential.id) {
      await deleteGitHubCredential(credential.id);
    }
  };

  const handleTest = async (credential: GitHubCredential) => {
    if (credential.owner && credential.repo) {
      await testGitHubConnection(credential.owner, credential.repo);
    } else {
      throw new Error("リポジトリ情報が不完全です");
    }
  };

  const handleSave = async (data: GitHubCredentialFormData) => {
    if (editingCredential) {
      // Update existing credential
      if (editingCredential.id) {
        await updateGitHubCredential(editingCredential.id, {
          apiKey: data.apiKey,
          baseUrl: data.baseUrl,
          owner: data.owner,
          repo: data.repo,
          isActive: true,
        });
      }
    } else {
      // Create new credential
      await saveGitHubCredential(data);
    }

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
          <Ionicons name="logo-github" size={24} color="#1f2937" />
          <Text className="text-xl font-bold text-gray-800 ml-2">
            GitHub認証情報
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
      {loading.github && (
        <View className="items-center py-8">
          <ActivityIndicator size="large" color="#267D00" />
          <Text className="text-gray-500 mt-2">読み込み中...</Text>
        </View>
      )}

      {/* Credentials List */}
      {!loading.github && (
        <>
          {gitHubCredentials.length > 0 ? (
            <View>
              {gitHubCredentials.map((credential) => (
                <CredentialCard
                  key={credential.id}
                  credential={credential}
                  serviceType="github"
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                  onTest={handleTest}
                />
              ))}
            </View>
          ) : (
            <View className="bg-gray-50 p-8 rounded-xl items-center">
              <Ionicons name="logo-github" size={48} color="gray" />
              <Text className="text-gray-500 mt-4 text-center font-medium">
                GitHub認証情報が設定されていません
              </Text>
              <Text className="text-gray-400 mt-2 text-center text-sm">
                コミットやPull Requestの情報を取得するために{"\n"}
                GitHub APIの認証情報を追加してください
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
      <View className="mt-6 bg-blue-50 p-4 rounded-lg">
        <View className="flex-row items-start">
          <Ionicons name="bulb" size={20} color="#3b82f6" />
          <View className="ml-2 flex-1">
            <Text className="text-blue-800 font-medium text-sm mb-1">
              GitHub認証情報について
            </Text>
            <Text className="text-blue-700 text-xs leading-4">
              • 複数のリポジトリやアカウントの認証情報を管理できます{"\n"}•
              Personal Access Tokenを使用して安全に認証します{"\n"}•
              日報生成時にコミットやPRの情報を自動取得します
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
        <GitHubCredentialForm
          initialData={editingCredential}
          onSave={handleSave}
          onCancel={handleCancel}
        />
      </Modal>
    </View>
  );
}
