import { useState, useEffect } from "react";
import { View, Text, TextInput, TouchableOpacity, ScrollView } from "react-native";

interface ReportEditorProps {
  content: string;
  onSave: (content: string) => void;
  isSaving?: boolean;
}

export default function ReportEditor({ content, onSave, isSaving = false }: ReportEditorProps) {
  const [editedContent, setEditedContent] = useState(content);
  const [hasChanges, setHasChanges] = useState(false);

  useEffect(() => {
    setEditedContent(content);
    setHasChanges(false);
  }, [content]);

  const handleContentChange = (text: string) => {
    setEditedContent(text);
    setHasChanges(text !== content);
  };

  const handleSave = () => {
    if (hasChanges && editedContent.trim()) {
      onSave(editedContent);
    }
  };

  const handleReset = () => {
    setEditedContent(content);
    setHasChanges(false);
  };

  return (
    <View className="flex-1 bg-white">
      {/* Editor Header */}
      <View className="bg-gray-50 border-b border-gray-200 p-4">
        <View className="flex-row justify-between items-center">
          <Text className="text-lg font-semibold text-gray-800">編集モード</Text>
          <View className="flex-row space-x-2">
            {hasChanges && (
              <TouchableOpacity
                onPress={handleReset}
                className="bg-gray-500 px-4 py-2 rounded-lg"
              >
                <Text className="text-white font-semibold">リセット</Text>
              </TouchableOpacity>
            )}
            <TouchableOpacity
              onPress={handleSave}
              disabled={!hasChanges || isSaving}
              className={`px-4 py-2 rounded-lg ${
                !hasChanges || isSaving
                  ? "bg-gray-300"
                  : "bg-primary"
              }`}
            >
              <Text className={`font-semibold ${
                !hasChanges || isSaving ? "text-gray-500" : "text-white"
              }`}>
                {isSaving ? "保存中..." : "保存"}
              </Text>
            </TouchableOpacity>
          </View>
        </View>
        {hasChanges && (
          <Text className="text-sm text-orange-600 mt-1">
            未保存の変更があります
          </Text>
        )}
      </View>

      {/* Editor Content */}
      <ScrollView className="flex-1 p-4">
        <View className="mb-4">
          <Text className="text-sm font-medium text-gray-700 mb-2">
            日報内容 (Markdown記法対応)
          </Text>
          <TextInput
            value={editedContent}
            onChangeText={handleContentChange}
            multiline
            placeholder="日報の内容を入力してください..."
            className="bg-gray-50 border border-gray-300 rounded-lg p-4 min-h-[400px] text-base leading-6"
            textAlignVertical="top"
            style={{
              fontFamily: "monospace", // Better for markdown editing
            }}
          />
        </View>

        {/* Markdown Help */}
        <View className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <Text className="font-semibold text-blue-800 mb-2">📝 Markdown記法ヘルプ</Text>
          <View className="space-y-1">
            <Text className="text-sm text-blue-700">
              <Text className="font-mono"># </Text>見出し1 / <Text className="font-mono">## </Text>見出し2
            </Text>
            <Text className="text-sm text-blue-700">
              <Text className="font-mono">- </Text>箇条書き / <Text className="font-mono">1. </Text>番号付きリスト
            </Text>
            <Text className="text-sm text-blue-700">
              <Text className="font-mono">**太字** </Text>/ <Text className="font-mono">*イタリック*</Text>
            </Text>
            <Text className="text-sm text-blue-700">
              <Text className="font-mono">`コード` </Text>/ <Text className="font-mono">```コードブロック```</Text>
            </Text>
          </View>
        </View>

        {/* Statistics */}
        <View className="mt-4 bg-gray-50 rounded-lg p-3">
          <View className="flex-row justify-between">
            <Text className="text-sm text-gray-600">
              文字数: {editedContent.length}
            </Text>
            <Text className="text-sm text-gray-600">
              行数: {editedContent.split('\n').length}
            </Text>
          </View>
        </View>
      </ScrollView>
    </View>
  );
}