import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  Switch,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import {
  togglCredentialSchema,
  type TogglCredentialFormData,
} from "../../schemas/togglCredential";
import type { components } from "@/types/api";

type TogglCredential = components["schemas"]["TogglCredentialResponseDto"];

interface TogglCredentialFormProps {
  initialData?: TogglCredential | null;
  onSave: (data: TogglCredentialFormData) => Promise<void>;
  onCancel: () => void;
}

export function TogglCredentialForm({
  initialData,
  onSave,
  onCancel,
}: TogglCredentialFormProps) {
  const [showApiKey, setShowApiKey] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [formData, setFormData] = useState<TogglCredentialFormData>({
    apiKey: initialData?.maskedApiKey?.includes("****")
      ? ""
      : initialData?.maskedApiKey || "",
    workspaceId: initialData?.workspaceId || undefined,
    projectIds: initialData?.projectIds || [],
    defaultTags: initialData?.defaultTags || [],
    timeZone: initialData?.timeZone || "UTC",
    includeWeekends: initialData?.includeWeekends || false,
  });
  const [errors, setErrors] = useState<Partial<Record<keyof TogglCredentialFormData, string>>>({});

  const validateForm = (): boolean => {
    const result = togglCredentialSchema.safeParse(formData);
    if (!result.success) {
      const newErrors: Partial<Record<keyof TogglCredentialFormData, string>> = {};
      result.error.issues.forEach(issue => {
        if (issue.path.length > 0) {
          const field = issue.path[0] as keyof TogglCredentialFormData;
          newErrors[field] = issue.message;
        }
      });
      setErrors(newErrors);
      return false;
    }
    setErrors({});
    return true;
  };

  const onSubmit = async () => {
    if (!validateForm()) return;
    
    setIsSaving(true);
    try {
      await onSave(formData);
    } catch {
      // Error handling is done in the hook
    } finally {
      setIsSaving(false);
    }
  };

  const updateField = (field: keyof TogglCredentialFormData, value: string | number | boolean | number[] | string[] | undefined) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  return (
    <ScrollView className="flex-1 bg-white">
      <View className="p-6">
        {/* Header */}
        <View className="flex-row items-center justify-between mb-6">
          <Text className="text-xl font-bold text-gray-800">
            Toggl認証情報{initialData ? "編集" : "追加"}
          </Text>
          <TouchableOpacity onPress={onCancel}>
            <Ionicons name="close" size={24} color="gray" />
          </TouchableOpacity>
        </View>

        <View>
          {/* API Key Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              Toggl Track API Token <Text className="text-red-500">*</Text>
            </Text>
            <View className="relative">
              <TextInput
                value={formData.apiKey}
                onChangeText={(text) => updateField("apiKey", text)}
                secureTextEntry={!showApiKey}
                placeholder="1234567890abcdef1234567890abcdef"
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
            {errors.apiKey && (
              <Text className="text-red-500 text-xs mt-1">
                {errors.apiKey}
              </Text>
            )}
            <Text className="text-gray-500 text-xs mt-1">
              Toggl Track → Profile Settings → API Tokenで取得
            </Text>
          </View>

          {/* Workspace ID Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              ワークスペースID
            </Text>
            <TextInput
              value={formData.workspaceId?.toString() || ""}
              onChangeText={(text) => {
                if (text === "") {
                  updateField("workspaceId", undefined);
                } else {
                  const num = parseInt(text, 10);
                  if (!isNaN(num)) {
                    updateField("workspaceId", num);
                  }
                }
              }}
              placeholder="12345"
              className="border border-gray-300 rounded-lg p-3 text-sm"
              keyboardType="numeric"
            />
            {errors.workspaceId && (
              <Text className="text-red-500 text-xs mt-1">
                {errors.workspaceId}
              </Text>
            )}
            <Text className="text-gray-500 text-xs mt-1">
              APIテスト後に自動取得されます（任意）
            </Text>
          </View>

          {/* Default Tags Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              デフォルトタグ
            </Text>
            <TextInput
              value={formData.defaultTags?.join(", ") || ""}
              onChangeText={(text) => {
                const tags = text
                  .split(",")
                  .map((tag) => tag.trim())
                  .filter((tag) => tag.length > 0);
                updateField("defaultTags", tags);
              }}
              placeholder="development, backend, project-name"
              className="border border-gray-300 rounded-lg p-3 text-sm"
              multiline
            />
            {errors.defaultTags && (
              <Text className="text-red-500 text-xs mt-1">
                {errors.defaultTags}
              </Text>
            )}
            <Text className="text-gray-500 text-xs mt-1">
              カンマ区切りで複数のタグを入力できます
            </Text>
          </View>

          {/* Time Zone Field */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              タイムゾーン
            </Text>
            <TextInput
              value={formData.timeZone}
              onChangeText={(text) => updateField("timeZone", text)}
              placeholder="Asia/Tokyo"
              className="border border-gray-300 rounded-lg p-3 text-sm"
            />
            {errors.timeZone && (
              <Text className="text-red-500 text-xs mt-1">
                {errors.timeZone}
              </Text>
            )}
            <Text className="text-gray-500 text-xs mt-1">
              例: Asia/Tokyo, America/New_York, Europe/London
            </Text>
          </View>

          {/* Include Weekends Switch */}
          <View className="mb-6">
            <View className="flex-row items-center justify-between">
              <View className="flex-1 mr-3">
                <Text className="text-sm font-medium text-gray-700 mb-1">
                  週末を含む
                </Text>
                <Text className="text-gray-500 text-xs">
                  日報生成時に土日の作業時間も含めます
                </Text>
              </View>
              <Switch
                value={formData.includeWeekends || false}
                onValueChange={(value) => updateField("includeWeekends", value)}
                trackColor={{ false: "#f3f4f6", true: "#267D00" }}
                thumbColor={"#ffffff"}
              />
            </View>
          </View>

          {/* Info Box */}
          <View className="bg-red-50 p-4 rounded-lg mb-6">
            <View className="flex-row items-start">
              <Ionicons name="information-circle" size={20} color="#ef4444" />
              <View className="ml-2 flex-1">
                <Text className="text-red-800 font-medium text-sm mb-1">
                  API Token取得方法
                </Text>
                <Text className="text-red-700 text-xs leading-4">
                  1. Toggl Trackにログイン{"\n"}
                  2. Profile Settings → API Token{"\n"}
                  3. 「Generate New Token」または既存のTokenをコピー{"\n"}
                  4. 必要な権限: Time Entry の読み取り
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
              onPress={onSubmit}
              disabled={isSaving}
              className={`flex-1 py-3 rounded-lg ${
                !isSaving ? "bg-primary" : "bg-gray-400"
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
