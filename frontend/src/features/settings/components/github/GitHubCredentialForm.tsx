import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  gitHubCredentialSchema,
  type GitHubCredentialFormData,
} from "../../schemas/gitHubCredential";
import type { components } from "@/types/api";

type GitHubCredential = components["schemas"]["GitHubCredentialResponseDto"];

interface GitHubCredentialFormProps {
  initialData?: GitHubCredential | null;
  onSave: (data: GitHubCredentialFormData) => Promise<void>;
  onCancel: () => void;
}

export function GitHubCredentialForm({
  initialData,
  onSave,
  onCancel,
}: GitHubCredentialFormProps) {
  const [showApiKey, setShowApiKey] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const form = useForm<GitHubCredentialFormData>({
    resolver: zodResolver(gitHubCredentialSchema),
    defaultValues: {
      apiKey: initialData?.maskedApiKey?.includes("****")
        ? ""
        : initialData?.maskedApiKey || "",
      baseUrl: initialData?.baseUrl || "https://api.github.com",
      owner: initialData?.owner || "",
      repo: initialData?.repo || "",
    },
  });

  const onSubmit = async (data: GitHubCredentialFormData) => {
    setIsSaving(true);
    try {
      await onSave(data);
    } catch (error) {
      // Error handling is done in the hook
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <ScrollView className="flex-1 bg-white">
      <View className="p-6">
        {/* Header */}
        <View className="flex-row items-center justify-between mb-6">
          <Text className="text-xl font-bold text-gray-800">
            GitHub認証情報{initialData ? "編集" : "追加"}
          </Text>
          <TouchableOpacity onPress={onCancel}>
            <Ionicons name="close" size={24} color="gray" />
          </TouchableOpacity>
        </View>

        <View>
          {/* API Key Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              GitHub API Token <Text className="text-red-500">*</Text>
            </Text>
            <View className="relative">
              <TextInput
                {...form.register("apiKey")}
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
            {form.formState.errors.apiKey && (
              <Text className="text-red-500 text-xs mt-1">
                {form.formState.errors.apiKey.message}
              </Text>
            )}
            <Text className="text-gray-500 text-xs mt-1">
              GitHub Settings → Developer settings → Personal access
              tokensで取得
            </Text>
          </View>

          {/* Base URL Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              API Base URL
            </Text>
            <TextInput
              {...form.register("baseUrl")}
              placeholder="https://api.github.com"
              className="border border-gray-300 rounded-lg p-3 text-sm"
              keyboardType="url"
            />
            {form.formState.errors.baseUrl && (
              <Text className="text-red-500 text-xs mt-1">
                {form.formState.errors.baseUrl.message}
              </Text>
            )}
            <Text className="text-gray-500 text-xs mt-1">
              GitHub Enterpriseの場合のみ変更してください
            </Text>
          </View>

          {/* Repository Info */}
          <View className="mb-6">
            <Text className="text-sm font-medium text-gray-700 mb-3">
              リポジトリ情報 <Text className="text-red-500">*</Text>
            </Text>

            <View className="flex-row space-x-3">
              {/* Owner Field */}
              <View className="flex-1">
                <Text className="text-xs text-gray-600 mb-1">Owner</Text>
                <TextInput
                  {...form.register("owner")}
                  placeholder="octocat"
                  className="border border-gray-300 rounded-lg p-3 text-sm"
                />
                {form.formState.errors.owner && (
                  <Text className="text-red-500 text-xs mt-1">
                    {form.formState.errors.owner.message}
                  </Text>
                )}
              </View>

              {/* Repo Field */}
              <View className="flex-1">
                <Text className="text-xs text-gray-600 mb-1">
                  Repository
                </Text>
                <TextInput
                  {...form.register("repo")}
                  placeholder="Hello-World"
                  className="border border-gray-300 rounded-lg p-3 text-sm"
                />
                {form.formState.errors.repo && (
                  <Text className="text-red-500 text-xs mt-1">
                    {form.formState.errors.repo.message}
                  </Text>
                )}
              </View>
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
                  • repo: リポジトリへの読み取りアクセス{"\n"}• user:
                  ユーザー情報の読み取り{"\n"}• read:org:
                  組織情報の読み取り（該当する場合）
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

            <TouchableOpacity
              onPress={form.handleSubmit(onSubmit)}
              disabled={form.formState.isSubmitting || isSaving}
              className={`flex-1 py-3 rounded-lg ${
                !form.formState.isSubmitting && !isSaving
                  ? "bg-primary"
                  : "bg-gray-400"
              }`}
            >
              <Text className="text-white text-center font-medium">
                {isSaving ? "保存中..." : initialData ? "更新" : "保存"}
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </ScrollView>
  );
}
