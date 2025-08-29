import { View, Text, TouchableOpacity } from "react-native";
import { Link } from "expo-router";
import { Ionicons } from "@expo/vector-icons";

export default function ReportsIndexScreen() {
  return (
    <View className="flex-1 bg-gray-50 p-6">
      <View className="mb-8">
        <Text className="text-2xl font-bold text-gray-800 mb-2">日報管理</Text>
        <Text className="text-gray-600">
          AIを活用した日報の生成・管理ができます
        </Text>
      </View>

      <View className="space-y-4">
        {/* Generate Report */}
        <Link href="/(tabs)/reports/generate" asChild>
          <TouchableOpacity className="bg-primary p-6 rounded-lg flex-row items-center">
            <View className="bg-white/20 p-3 rounded-lg mr-4">
              <Ionicons name="add-circle" size={32} color="white" />
            </View>
            <View className="flex-1">
              <Text className="text-white text-xl font-semibold mb-1">
                新しい日報を生成
              </Text>
              <Text className="text-white/80 text-sm">
                GitHub、Toggl Track、Notionから情報を収集
              </Text>
            </View>
            <Ionicons name="chevron-forward" size={24} color="white" />
          </TouchableOpacity>
        </Link>

        {/* View History - Placeholder for Issue #16 */}
        <TouchableOpacity 
          className="bg-white border border-gray-200 p-6 rounded-lg flex-row items-center"
          onPress={() => {
            // Placeholder - will be implemented in Issue #16
            alert("日報履歴機能は準備中です");
          }}
        >
          <View className="bg-gray-100 p-3 rounded-lg mr-4">
            <Ionicons name="document-text" size={32} color="#267D00" />
          </View>
          <View className="flex-1">
            <Text className="text-gray-800 text-xl font-semibold mb-1">
              日報履歴
            </Text>
            <Text className="text-gray-600 text-sm">
              過去の日報を確認・編集
            </Text>
          </View>
          <Ionicons name="chevron-forward" size={24} color="#9CA3AF" />
        </TouchableOpacity>

        {/* Settings - Placeholder for Issue #14 */}
        <TouchableOpacity 
          className="bg-white border border-gray-200 p-6 rounded-lg flex-row items-center"
          onPress={() => {
            // Placeholder - will be implemented in Issue #14
            alert("API設定機能は準備中です");
          }}
        >
          <View className="bg-gray-100 p-3 rounded-lg mr-4">
            <Ionicons name="settings" size={32} color="#267D00" />
          </View>
          <View className="flex-1">
            <Text className="text-gray-800 text-xl font-semibold mb-1">
              API設定
            </Text>
            <Text className="text-gray-600 text-sm">
              GitHub、Toggl Track、Notion連携設定
            </Text>
          </View>
          <Ionicons name="chevron-forward" size={24} color="#9CA3AF" />
        </TouchableOpacity>
      </View>

      {/* Help Section */}
      <View className="mt-8 bg-blue-50 p-4 rounded-lg">
        <Text className="text-blue-800 font-semibold mb-2">💡 使い方</Text>
        <Text className="text-blue-700 text-sm leading-5">
          1. 「新しい日報を生成」で日付を選択{"\n"}
          2. 必要に応じて追加メモを入力{"\n"}
          3. AIが自動で日報を生成{"\n"}
          4. 内容を確認・編集して完成
        </Text>
      </View>
    </View>
  );
}