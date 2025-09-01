import { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  TextInput,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import DateInput from "./DateInput";

export interface FilterOptions {
  startDate?: string;
  endDate?: string;
  searchText?: string;
}

interface ReportFilterProps {
  filters: FilterOptions;
  onFiltersChange: (filters: FilterOptions) => void;
  onClearFilters: () => void;
}


export default function ReportFilter({
  filters,
  onFiltersChange,
  onClearFilters,
}: ReportFilterProps) {
  const [isExpanded, setIsExpanded] = useState(false);



  const handleSearchTextChange = (text: string) => {
    onFiltersChange({
      ...filters,
      searchText: text,
    });
  };

  const getActiveFiltersCount = () => {
    let count = 0;
    if (filters.startDate || filters.endDate) count++;
    if (filters.searchText && filters.searchText.trim()) count++;
    return count;
  };

  const hasActiveFilters = getActiveFiltersCount() > 0;


  return (
    <View className="bg-white border-b border-gray-200">
      {/* Search Bar */}
      <View className="p-4">
        <View className="flex-row items-center bg-gray-50 rounded-lg px-3 py-2">
          <Ionicons name="search" size={20} color="#6B7280" />
          <TextInput
            className="flex-1 ml-2 text-gray-800"
            placeholder="日報を検索..."
            value={filters.searchText || ""}
            onChangeText={handleSearchTextChange}
          />
          {filters.searchText && (
            <TouchableOpacity
              onPress={() => handleSearchTextChange("")}
              className="p-1"
            >
              <Ionicons name="close-circle" size={20} color="#6B7280" />
            </TouchableOpacity>
          )}
        </View>
      </View>

      {/* Filter Toggle */}
      <TouchableOpacity
        className="flex-row items-center justify-between px-4 py-3 border-t border-gray-100"
        onPress={() => setIsExpanded(!isExpanded)}
      >
        <View className="flex-row items-center">
          <Ionicons name="filter" size={20} color="#6B7280" />
          <Text className="text-gray-700 ml-2 font-medium">フィルター</Text>
          {hasActiveFilters && (
            <View className="bg-blue-500 rounded-full w-5 h-5 items-center justify-center ml-2">
              <Text className="text-white text-xs font-bold">
                {getActiveFiltersCount()}
              </Text>
            </View>
          )}
        </View>
        <Ionicons
          name={isExpanded ? "chevron-up" : "chevron-down"}
          size={20}
          color="#6B7280"
        />
      </TouchableOpacity>

      {/* Filter Options */}
      {isExpanded && (
        <View className="px-4 pb-4 bg-gray-50">
          {/* Date Range */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">
              日付範囲
            </Text>
            <View className="flex-row space-x-3">
              <View className="flex-1 bg-white border border-gray-200 rounded-lg p-3">
                <Text className="text-xs text-gray-500 mb-1">開始日</Text>
                <DateInput
                  value={filters.startDate}
                  onChange={(dateString) =>
                    onFiltersChange({
                      ...filters,
                      startDate: dateString,
                    })
                  }
                />
              </View>

              <View className="flex-1 bg-white border border-gray-200 rounded-lg p-3">
                <Text className="text-xs text-gray-500 mb-1">終了日</Text>
                <DateInput
                  value={filters.endDate}
                  onChange={(dateString) =>
                    onFiltersChange({
                      ...filters,
                      endDate: dateString,
                    })
                  }
                />
              </View>
            </View>
          </View>


          {/* Clear Filters */}
          {hasActiveFilters && (
            <TouchableOpacity
              className="bg-gray-200 rounded-lg p-3 items-center"
              onPress={onClearFilters}
            >
              <Text className="text-gray-700 font-medium">
                フィルターをクリア
              </Text>
            </TouchableOpacity>
          )}
        </View>
      )}

    </View>
  );
}
