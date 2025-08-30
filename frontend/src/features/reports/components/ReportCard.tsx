import { View, Text, TouchableOpacity } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { router } from "expo-router";
import { components } from "../../../../types/api";

type DailyReportDto = components["schemas"]["DailyReportDto"];

interface ReportCardProps {
  report: DailyReportDto;
}

const getStatusConfig = (status?: string) => {
  switch (status) {
    case "DRAFT":
      return {
        label: "下書き",
        bgColor: "bg-yellow-100",
        textColor: "text-yellow-800",
        iconColor: "#D97706",
      };
    case "EDITED":
      return {
        label: "編集済み",
        bgColor: "bg-blue-100",
        textColor: "text-blue-800",
        iconColor: "#2563EB",
      };
    case "APPROVED":
      return {
        label: "承認済み",
        bgColor: "bg-green-100",
        textColor: "text-green-800",
        iconColor: "#16A34A",
      };
    default:
      return {
        label: "不明",
        bgColor: "bg-gray-100",
        textColor: "text-gray-800",
        iconColor: "#6B7280",
      };
  }
};

const formatDate = (dateString?: string) => {
  if (!dateString) return "日付不明";
  
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString("ja-JP", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      weekday: "short",
    });
  } catch {
    return dateString;
  }
};

const formatDateTime = (dateString?: string) => {
  if (!dateString) return "不明";
  
  try {
    const date = new Date(dateString);
    return date.toLocaleString("ja-JP", {
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return dateString;
  }
};

export default function ReportCard({ report }: ReportCardProps) {
  const statusConfig = getStatusConfig(report.status);
  
  const handlePress = () => {
    if (report.reportDate) {
      router.push(`/reports/${report.reportDate}`);
    }
  };

  return (
    <TouchableOpacity
      className="bg-white border border-gray-200 rounded-lg p-4 mb-3 shadow-sm"
      onPress={handlePress}
      activeOpacity={0.7}
    >
      {/* Header: Date and Status */}
      <View className="flex-row justify-between items-center mb-3">
        <Text className="text-lg font-bold text-gray-800">
          {formatDate(report.reportDate)}
        </Text>
        <View className={`px-3 py-1 rounded-full ${statusConfig.bgColor}`}>
          <Text className={`text-sm font-semibold ${statusConfig.textColor}`}>
            {statusConfig.label}
          </Text>
        </View>
      </View>

      {/* Content Preview */}
      <View className="mb-3">
        <Text className="text-gray-600 text-sm leading-5" numberOfLines={3}>
          {report.displayContent || report.finalContent || report.editedContent || report.generatedContent || "内容なし"}
        </Text>
      </View>

      {/* Meta Information */}
      <View className="flex-row justify-between items-center">
        <View className="flex-row items-center space-x-4">
          {/* Generation Count */}
          <View className="flex-row items-center">
            <Ionicons name="refresh" size={16} color="#6B7280" />
            <Text className="text-gray-500 text-xs ml-1">
              {report.generationCount || 0}回生成
            </Text>
          </View>
          
          {/* Last Updated */}
          <View className="flex-row items-center">
            <Ionicons name="time" size={16} color="#6B7280" />
            <Text className="text-gray-500 text-xs ml-1">
              {formatDateTime(report.updatedAt)}
            </Text>
          </View>
        </View>

        {/* Navigation Icon */}
        <Ionicons name="chevron-forward" size={20} color="#9CA3AF" />
      </View>
    </TouchableOpacity>
  );
}