import { useState } from "react";
import { View } from "react-native";
import ReportFilter, { FilterOptions } from "../../src/features/reports/components/ReportFilter";
import ReportList from "../../src/features/reports/components/ReportList";
import useReports from "../../src/features/reports/hooks/useReports";

export default function ReportHistoryScreen() {
  const [filters, setFilters] = useState<FilterOptions>({});
  
  const {
    reports,
    loading,
    refreshing,
    loadingMore,
    error,
    fetchReports,
    refreshReports,
    loadMoreReports,
  } = useReports();

  const handleFiltersChange = (newFilters: FilterOptions) => {
    setFilters(newFilters);
    fetchReports(newFilters);
  };

  const handleClearFilters = () => {
    const clearedFilters: FilterOptions = {};
    setFilters(clearedFilters);
    fetchReports(clearedFilters);
  };

  return (
    <View className="flex-1 bg-gray-50">
      <ReportFilter
        filters={filters}
        onFiltersChange={handleFiltersChange}
        onClearFilters={handleClearFilters}
      />
      
      <ReportList
        reports={reports}
        loading={loading}
        refreshing={refreshing}
        onRefresh={refreshReports}
        onEndReached={loadMoreReports}
        loadingMore={loadingMore}
        error={error}
      />
    </View>
  );
}