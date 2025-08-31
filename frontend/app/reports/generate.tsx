import { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, ScrollView, Alert } from "react-native";
import { router } from "expo-router";


export default function GenerateReportScreen() {
  const [selectedDate, setSelectedDate] = useState(() => {
    const today = new Date();
    return today.toISOString().split('T')[0]; // YYYY-MM-DD format
  });
  const [additionalNotes, setAdditionalNotes] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);

  const handleDateChange = (text: string) => {
    // Basic date validation (YYYY-MM-DD format)
    if (/^\d{4}-\d{2}-\d{2}$/.test(text) || text.length <= 10) {
      setSelectedDate(text);
    }
  };

  const handleGenerate = async () => {
    if (!selectedDate || selectedDate.length !== 10) {
      Alert.alert("エラー", "正しい日付を入力してください (YYYY-MM-DD)");
      return;
    }

    setIsGenerating(true);
    
    try {
      // Mock request data - later will be replaced with actual API call

      // Mock delay to simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // For now, just navigate to the report detail page
      router.push(`/reports/${selectedDate}`);
      
      Alert.alert("成功", "日報が生成されました！");
      
    } catch (error) {
      console.error("Report generation failed:", error);
      Alert.alert("エラー", "日報の生成に失敗しました。もう一度お試しください。");
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <ScrollView className="flex-1 bg-gray-50">
      <View className="p-6">
        <Text className="text-2xl font-bold text-gray-800 mb-6">日報生成</Text>
        
        {/* Date Selection */}
        <View className="mb-6">
          <Text className="text-lg font-semibold text-gray-700 mb-2">対象日付</Text>
          <TextInput
            value={selectedDate}
            onChangeText={handleDateChange}
            placeholder="YYYY-MM-DD"
            className="bg-white border border-gray-300 rounded-lg px-4 py-3 text-base"
            maxLength={10}
          />
          <Text className="text-sm text-gray-500 mt-1">
            例: {new Date().toISOString().split('T')[0]}
          </Text>
        </View>

        {/* Additional Notes */}
        <View className="mb-8">
          <Text className="text-lg font-semibold text-gray-700 mb-2">追加メモ</Text>
          <TextInput
            value={additionalNotes}
            onChangeText={setAdditionalNotes}
            placeholder="特記事項やコメントがあれば入力してください..."
            multiline
            numberOfLines={4}
            className="bg-white border border-gray-300 rounded-lg px-4 py-3 text-base h-24"
            textAlignVertical="top"
          />
          <Text className="text-sm text-gray-500 mt-1">
            AIがこの情報を参考にして日報を生成します
          </Text>
        </View>

        {/* Generate Button */}
        <TouchableOpacity
          onPress={handleGenerate}
          disabled={isGenerating}
          className={`py-4 px-6 rounded-lg ${
            isGenerating 
              ? "bg-gray-400" 
              : "bg-primary"
          }`}
        >
          <Text className="text-white text-center font-semibold text-lg">
            {isGenerating ? "生成中..." : "日報を生成"}
          </Text>
        </TouchableOpacity>

        {/* Help Text */}
        <View className="mt-8 bg-blue-50 p-4 rounded-lg">
          <Text className="text-blue-800 font-semibold mb-2">💡 日報生成について</Text>
          <Text className="text-blue-700 text-sm leading-5">
            • GitHub、Toggl Track、Notionから自動的にデータを収集します{"\n"}
            • AIが作業内容や学びを整理して日報を生成します{"\n"}
            • 生成後は編集・修正が可能です
          </Text>
        </View>
      </View>
    </ScrollView>
  );
}