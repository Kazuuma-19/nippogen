import { useState, useEffect, useCallback } from "react";
import { View, Text, ScrollView, TouchableOpacity, Alert } from "react-native";
import { useLocalSearchParams, router } from "expo-router";
import type { components } from "@/types/api";

// Import components that will be implemented next
import ReportEditor from "@/src/features/reports/components/ReportEditor";
import MarkdownPreview from "@/src/features/reports/components/MarkdownPreview";

type DailyReport = components["schemas"]["DailyReportDto"];

export default function ReportDetailScreen() {
  const { date } = useLocalSearchParams<{ date: string }>();
  const [report, setReport] = useState<DailyReport | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);

  const loadReport = useCallback(async () => {
    if (!date) return;

    setIsLoading(true);
    try {
      // Mock data - later replace with actual API call
      const mockReport: DailyReport = {
        id: "mock-report-id",
        userId: "mock-user-id",
        reportDate: date,
        generatedContent: `# ${date} の日報

## 今日やったこと
- GitHub: 3つのPRをレビュー
- 新機能の実装: ユーザー認証機能
- バグ修正: ログイン画面の表示崩れを修正

## 学んだこと
- React Nativeでの状態管理のベストプラクティス
- TypeScriptの型安全性の重要性

## 明日やること
- APIドキュメントの更新
- テストカバレッジの向上
- コードレビューの実施

## 所感
今日は効率的に作業を進めることができました。特に新しいTypeScriptの機能を学ぶことができて有意義でした。`,
        editedContent: undefined,
        generationCount: 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        displayContent: undefined
      };

      await new Promise(resolve => setTimeout(resolve, 1000));
      setReport(mockReport);
    } catch (error) {
      console.error("Failed to load report:", error);
      Alert.alert("エラー", "日報の読み込みに失敗しました");
    } finally {
      setIsLoading(false);
    }
  }, [date]);

  useEffect(() => {
    loadReport();
  }, [loadReport]);

  const handleSave = async (content: string) => {
    if (!report) return;

    setIsSaving(true);
    try {
      // Mock save - later replace with actual API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      setReport({
        ...report,
        editedContent: content,
        updatedAt: new Date().toISOString()
      });
      
      setIsEditing(false);
      Alert.alert("成功", "日報を保存しました");
    } catch (error) {
      console.error("Failed to save report:", error);
      Alert.alert("エラー", "日報の保存に失敗しました");
    } finally {
      setIsSaving(false);
    }
  };

  const handleRegenerate = async () => {
    if (!report) return;

    Alert.alert(
      "日報再生成",
      "AIに日報を再生成させますか？現在の編集内容は失われます。",
      [
        { text: "キャンセル", style: "cancel" },
        {
          text: "再生成",
          style: "destructive",
          onPress: async () => {
            setIsLoading(true);
            try {
              // Mock regeneration - later replace with actual API call
              await new Promise(resolve => setTimeout(resolve, 2000));
              loadReport(); // Reload the report
              Alert.alert("成功", "日報を再生成しました");
            } catch {
              Alert.alert("エラー", "日報の再生成に失敗しました");
            } finally {
              setIsLoading(false);
            }
          }
        }
      ]
    );
  };


  if (isLoading) {
    return (
      <View className="flex-1 justify-center items-center bg-gray-50">
        <Text className="text-lg text-gray-600">読み込み中...</Text>
      </View>
    );
  }

  if (!report) {
    return (
      <View className="flex-1 justify-center items-center bg-gray-50 p-6">
        <Text className="text-xl text-gray-800 mb-4">日報が見つかりません</Text>
        <TouchableOpacity 
          onPress={() => router.back()}
          className="bg-primary px-6 py-3 rounded-lg"
        >
          <Text className="text-white font-semibold">戻る</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const currentContent = report.editedContent || report.generatedContent || "";

  return (
    <View className="flex-1 bg-gray-50">
      {/* Header */}
      <View className="bg-white border-b border-gray-200 p-4">
        <View className="flex-row justify-between items-center">
          <View>
            <Text className="text-xl font-bold text-gray-800">{date} の日報</Text>
          </View>
          
          <View className="flex-row space-x-2">
            <TouchableOpacity
              onPress={() => setIsEditing(!isEditing)}
              className="bg-primary px-4 py-2 rounded-lg"
            >
              <Text className="text-white font-semibold">
                {isEditing ? "プレビュー" : "編集"}
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>

      {/* Content */}
      <ScrollView className="flex-1">
        {isEditing ? (
          <ReportEditor
            content={currentContent}
            onSave={handleSave}
            isSaving={isSaving}
          />
        ) : (
          <MarkdownPreview content={currentContent} />
        )}
      </ScrollView>

      {/* Actions */}
      {!isEditing && (
        <View className="bg-white border-t border-gray-200 p-4">
          <View className="flex-row justify-around">
            <TouchableOpacity
              onPress={handleRegenerate}
              className="bg-orange-500 px-4 py-2 rounded-lg"
            >
              <Text className="text-white font-semibold">再生成</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}
    </View>
  );
}
