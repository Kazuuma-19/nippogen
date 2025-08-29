import { View, Text, TouchableOpacity } from "react-native";
import { Link } from "expo-router";

export default function HomeScreen() {
  return (
    <View className="flex-1 bg-gray-50 p-6">
      <View className="flex-1 justify-center items-center">
        <Text className="text-3xl font-bold text-gray-800 mb-2">Nippogen</Text>
        <Text className="text-lg text-gray-600 mb-8 text-center">
          AI powered daily report generator
        </Text>
        
        <View className="w-full max-w-sm space-y-4">
          <Link href="/(tabs)/reports/generate" asChild>
            <TouchableOpacity className="bg-primary p-4 rounded-lg">
              <Text className="text-white text-center font-semibold text-lg">
                日報を生成
              </Text>
            </TouchableOpacity>
          </Link>
          
          <Link href="/reports" asChild>
            <TouchableOpacity className="bg-white border-2 border-primary p-4 rounded-lg">
              <Text className="text-primary text-center font-semibold text-lg">
                日報履歴
              </Text>
            </TouchableOpacity>
          </Link>
        </View>
      </View>
    </View>
  );
}