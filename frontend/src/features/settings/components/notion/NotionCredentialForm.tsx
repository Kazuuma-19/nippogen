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
  notionCredentialSchema,
  type NotionCredentialFormData,
} from "../../schemas/notionCredential";
import type { components } from "@/types/api";

type NotionCredential = components["schemas"]["NotionCredentialResponseDto"];

interface NotionCredentialFormProps {
  initialData?: NotionCredential | null;
  onSave: (data: NotionCredentialFormData) => Promise<void>;
  onCancel: () => void;
}

export function NotionCredentialForm({
  initialData,
  onSave,
  onCancel,
}: NotionCredentialFormProps) {
  const [showApiKey, setShowApiKey] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const form = useForm<NotionCredentialFormData>({
    resolver: zodResolver(notionCredentialSchema),
    defaultValues: {
      apiKey: initialData?.maskedApiKey?.includes("****")
        ? ""
        : initialData?.maskedApiKey || "",
      databaseId: initialData?.databaseId || "",
      titleProperty: initialData?.titleProperty || "Name",
      statusProperty: initialData?.statusProperty || "Status",
      dateProperty: initialData?.dateProperty || "Date",
      filterConditions: initialData?.filterConditions || {},
    },
  });

  const onSubmit = async (data: NotionCredentialFormData) => {
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
            Notion認証情報{initialData ? "編集" : "追加"}
          </Text>
          <TouchableOpacity onPress={onCancel}>
            <Ionicons name="close" size={24} color="gray" />
          </TouchableOpacity>
        </View>

        <View>
          {/* API Key Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              Notion Integration Token{" "}
              <Text className="text-red-500">*</Text>
            </Text>
            <View className="relative">
              <TextInput
                {...form.register("apiKey")}
                secureTextEntry={!showApiKey}
                placeholder="secret_xxxxxxxxxxxxxxxxxxxx"
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
              Notion → Settings → Integrations → New integrationで作成
            </Text>
          </View>

          {/* Database ID Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              データベースID
            </Text>
            <TextInput
              {...form.register("databaseId")}
              placeholder="123e4567-e89b-12d3-a456-426614174000"
              className="border border-gray-300 rounded-lg p-3 text-sm"
            />
            {form.formState.errors.databaseId && (
              <Text className="text-red-500 text-xs mt-1">
                {form.formState.errors.databaseId.message}
              </Text>
            )}
            <Text className="text-gray-500 text-xs mt-1">
              データベースURLから32文字のUUIDを取得してください
            </Text>
          </View>

          {/* Database Properties */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-3">
              データベースプロパティ設定
            </Text>

            {/* Title Property */}
            <View className="mb-3">
              <Text className="text-xs text-gray-600 mb-1">
                タイトルプロパティ名
              </Text>
              <TextInput
                {...form.register("titleProperty")}
                placeholder="Name"
                className="border border-gray-300 rounded-lg p-3 text-sm"
              />
              {form.formState.errors.titleProperty && (
                <Text className="text-red-500 text-xs mt-1">
                  {form.formState.errors.titleProperty.message}
                </Text>
              )}
            </View>

            {/* Status Property */}
            <View className="mb-3">
              <Text className="text-xs text-gray-600 mb-1">
                ステータスプロパティ名
              </Text>
              <TextInput
                {...form.register("statusProperty")}
                placeholder="Status"
                className="border border-gray-300 rounded-lg p-3 text-sm"
              />
              {form.formState.errors.statusProperty && (
                <Text className="text-red-500 text-xs mt-1">
                  {form.formState.errors.statusProperty.message}
                </Text>
              )}
            </View>

            {/* Date Property */}
            <View className="mb-3">
              <Text className="text-xs text-gray-600 mb-1">
                日付プロパティ名
              </Text>
              <TextInput
                {...form.register("dateProperty")}
                placeholder="Date"
                className="border border-gray-300 rounded-lg p-3 text-sm"
              />
              {form.formState.errors.dateProperty && (
                <Text className="text-red-500 text-xs mt-1">
                  {form.formState.errors.dateProperty.message}
                </Text>
              )}
            </View>
          </View>

          {/* Info Boxes */}
          <View className="bg-gray-50 p-4 rounded-lg mb-4">
            <View className="flex-row items-start">
              <Ionicons name="information-circle" size={20} color="#6b7280" />
              <View className="ml-2 flex-1">
                <Text className="text-gray-800 font-medium text-sm mb-1">
                  Integration作成手順
                </Text>
                <Text className="text-gray-700 text-xs leading-4">
                  1. notion.so/my-integrations にアクセス{"\n"}
                  2. 「+ New integration」をクリック{"\n"}
                  3. 名前を設定（例：Nippogen）{"\n"}
                  4. 「Submit」をクリックしてトークンをコピー
                </Text>
              </View>
            </View>
          </View>

          <View className="bg-yellow-50 p-4 rounded-lg mb-6">
            <View className="flex-row items-start">
              <Ionicons name="warning" size={20} color="#f59e0b" />
              <View className="ml-2 flex-1">
                <Text className="text-yellow-800 font-medium text-sm mb-1">
                  データベース接続設定
                </Text>
                <Text className="text-yellow-700 text-xs leading-4">
                  Integrationを作成後、使用するNotionデータベースの{"\n"}
                  「...」→「Connect to」→作成したIntegrationを選択{"\n"}
                  してください。これによりAPIからアクセス可能になります。
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
