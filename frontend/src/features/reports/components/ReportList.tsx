import { FlatList, View, Text, RefreshControl, ActivityIndicator } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import ReportCard from "./ReportCard";
import { components } from "../../../../types/api";

type DailyReportDto = components["schemas"]["DailyReportDto"];

interface ReportListProps {
  reports: DailyReportDto[];
  loading?: boolean;
  refreshing?: boolean;
  onRefresh?: () => void;
  onEndReached?: () => void;
  loadingMore?: boolean;
  error?: string | null;
}

const EmptyState = ({ message }: { message: string }) => (
  <View className="flex-1 justify-center items-center py-16">
    <View className="bg-gray-100 p-6 rounded-full mb-4">
      <Ionicons name="document-text-outline" size={48} color="#9CA3AF" />
    </View>
    <Text className="text-gray-500 text-lg font-medium mb-2">日報が見つかりません</Text>
    <Text className="text-gray-400 text-center leading-6 max-w-xs">
      {message}
    </Text>
  </View>
);

const ErrorState = ({ message }: { message: string }) => (
  <View className="flex-1 justify-center items-center py-16">
    <View className="bg-red-100 p-6 rounded-full mb-4">
      <Ionicons name="warning-outline" size={48} color="#EF4444" />
    </View>
    <Text className="text-red-600 text-lg font-medium mb-2">エラーが発生しました</Text>
    <Text className="text-red-500 text-center leading-6 max-w-xs">
      {message}
    </Text>
  </View>
);

const LoadingState = () => (
  <View className="flex-1 justify-center items-center py-16">
    <ActivityIndicator size="large" color="#2563EB" />
    <Text className="text-gray-500 mt-4">日報を読み込んでいます...</Text>
  </View>
);

const LoadMoreIndicator = () => (
  <View className="py-4 items-center">
    <ActivityIndicator size="small" color="#2563EB" />
    <Text className="text-gray-500 text-sm mt-2">さらに読み込んでいます...</Text>
  </View>
);

export default function ReportList({
  reports,
  loading = false,
  refreshing = false,
  onRefresh,
  onEndReached,
  loadingMore = false,
  error,
}: ReportListProps) {
  const renderReportCard = ({ item }: { item: DailyReportDto }) => (
    <ReportCard report={item} />
  );

  const renderFooter = () => {
    if (loadingMore) {
      return <LoadMoreIndicator />;
    }
    return null;
  };

  const getItemLayout = (_: any, index: number) => ({
    length: 120, // Approximate height of ReportCard
    offset: 120 * index,
    index,
  });

  // Show error state
  if (error && !refreshing) {
    return <ErrorState message={error} />;
  }

  // Show loading state for initial load
  if (loading && reports.length === 0) {
    return <LoadingState />;
  }

  // Show empty state when no reports
  if (!loading && reports.length === 0) {
    return (
      <EmptyState 
        message="フィルター条件を変更するか、新しい日報を作成してみてください。" 
      />
    );
  }

  return (
    <FlatList
      data={reports}
      renderItem={renderReportCard}
      keyExtractor={(item) => item.id || `report-${item.reportDate || Math.random()}`}
      contentContainerStyle={{ padding: 16 }}
      showsVerticalScrollIndicator={false}
      refreshControl={
        onRefresh ? (
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            colors={["#2563EB"]} // Android
            tintColor="#2563EB" // iOS
          />
        ) : undefined
      }
      onEndReached={onEndReached}
      onEndReachedThreshold={0.1}
      ListFooterComponent={renderFooter}
      getItemLayout={getItemLayout}
      removeClippedSubviews={true}
      maxToRenderPerBatch={10}
      updateCellsBatchingPeriod={50}
      initialNumToRender={10}
    />
  );
}