import { useState } from "react";
import { View, Text, TouchableOpacity, TextInput, Modal, Platform } from "react-native";
import { Ionicons } from "@expo/vector-icons";

// Conditional import for DateTimePicker (not available on web)
let DateTimePicker: any = null;
if (Platform.OS !== 'web') {
  DateTimePicker = require('@react-native-community/datetimepicker').default;
}

export interface FilterOptions {
  startDate?: string;
  endDate?: string;
  status?: "DRAFT" | "EDITED" | "APPROVED";
  searchText?: string;
}

interface ReportFilterProps {
  filters: FilterOptions;
  onFiltersChange: (filters: FilterOptions) => void;
  onClearFilters: () => void;
}

const statusOptions = [
  { value: undefined, label: "すべて" },
  { value: "DRAFT" as const, label: "下書き" },
  { value: "EDITED" as const, label: "編集済み" },
  { value: "APPROVED" as const, label: "承認済み" },
];

export default function ReportFilter({ filters, onFiltersChange, onClearFilters }: ReportFilterProps) {
  const [showDatePicker, setShowDatePicker] = useState<"start" | "end" | null>(null);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);
  const [tempDate, setTempDate] = useState(new Date());

  const formatDateForDisplay = (dateString?: string) => {
    if (!dateString) return "選択してください";
    
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString("ja-JP", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      });
    } catch {
      return "選択してください";
    }
  };

  const handleDateChange = (event: any, selectedDate?: Date) => {
    if (Platform.OS === 'android') {
      setShowDatePicker(null);
    }
    
    if (selectedDate) {
      setTempDate(selectedDate);
      
      // Android: immediately apply the date
      if (Platform.OS === 'android' && showDatePicker) {
        const dateString = selectedDate.toISOString().split('T')[0];
        onFiltersChange({
          ...filters,
          [showDatePicker === "start" ? "startDate" : "endDate"]: dateString,
        });
      }
    }
  };

  const confirmDateSelection = () => {
    if (showDatePicker) {
      const dateString = tempDate.toISOString().split('T')[0];
      onFiltersChange({
        ...filters,
        [showDatePicker === "start" ? "startDate" : "endDate"]: dateString,
      });
    }
    setShowDatePicker(null);
  };

  const cancelDateSelection = () => {
    setShowDatePicker(null);
  };

  const handleStatusSelect = (status: FilterOptions["status"]) => {
    onFiltersChange({
      ...filters,
      status,
    });
    setShowStatusModal(false);
  };

  const handleSearchTextChange = (text: string) => {
    onFiltersChange({
      ...filters,
      searchText: text,
    });
  };

  const getActiveFiltersCount = () => {
    let count = 0;
    if (filters.startDate || filters.endDate) count++;
    if (filters.status) count++;
    if (filters.searchText && filters.searchText.trim()) count++;
    return count;
  };

  const hasActiveFilters = getActiveFiltersCount() > 0;

  const getStatusLabel = (status?: string) => {
    const option = statusOptions.find(opt => opt.value === status);
    return option?.label || "すべて";
  };

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
              <Text className="text-white text-xs font-bold">{getActiveFiltersCount()}</Text>
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
            <Text className="text-sm font-medium text-gray-700 mb-2">日付範囲</Text>
            <View className="flex-row space-x-3">
              <TouchableOpacity
                className="flex-1 bg-white border border-gray-200 rounded-lg p-3"
                onPress={() => {
                  if (filters.startDate) {
                    setTempDate(new Date(filters.startDate));
                  }
                  setShowDatePicker("start");
                }}
              >
                <Text className="text-xs text-gray-500 mb-1">開始日</Text>
                <Text className="text-gray-800">
                  {formatDateForDisplay(filters.startDate)}
                </Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                className="flex-1 bg-white border border-gray-200 rounded-lg p-3"
                onPress={() => {
                  if (filters.endDate) {
                    setTempDate(new Date(filters.endDate));
                  }
                  setShowDatePicker("end");
                }}
              >
                <Text className="text-xs text-gray-500 mb-1">終了日</Text>
                <Text className="text-gray-800">
                  {formatDateForDisplay(filters.endDate)}
                </Text>
              </TouchableOpacity>
            </View>
          </View>

          {/* Status Filter */}
          <View className="mb-4">
            <Text className="text-sm font-medium text-gray-700 mb-2">ステータス</Text>
            <TouchableOpacity
              className="bg-white border border-gray-200 rounded-lg p-3"
              onPress={() => setShowStatusModal(true)}
            >
              <Text className="text-gray-800">{getStatusLabel(filters.status)}</Text>
            </TouchableOpacity>
          </View>

          {/* Clear Filters */}
          {hasActiveFilters && (
            <TouchableOpacity
              className="bg-gray-200 rounded-lg p-3 items-center"
              onPress={onClearFilters}
            >
              <Text className="text-gray-700 font-medium">フィルターをクリア</Text>
            </TouchableOpacity>
          )}
        </View>
      )}

      {/* Date Picker Modal */}
      {showDatePicker && (
        <>
          {Platform.OS === 'web' ? (
            // Web: HTML input type="date" with calendar
            <Modal
              visible={true}
              transparent={true}
              animationType="fade"
              onRequestClose={cancelDateSelection}
            >
              <TouchableOpacity 
                className="flex-1 justify-center items-center bg-black/50"
                activeOpacity={1}
                onPress={cancelDateSelection}
              >
                <TouchableOpacity 
                  className="bg-white rounded-lg p-6 m-4 min-w-[320px] max-w-[400px]"
                  activeOpacity={1}
                  onPress={(e) => e.stopPropagation()}
                >
                  <Text className="text-lg font-semibold text-gray-800 mb-4 text-center">
                    {showDatePicker === "start" ? "開始日を選択" : "終了日を選択"}
                  </Text>
                  
                  <View className="mb-6">
                    <input
                      type="date"
                      value={tempDate.toISOString().split('T')[0]}
                      onChange={(e: any) => {
                        const date = new Date(e.target.value);
                        setTempDate(date);
                      }}
                      style={{
                        width: '100%',
                        padding: '12px',
                        border: '1px solid #d1d5db',
                        borderRadius: '8px',
                        fontSize: '16px',
                        outline: 'none',
                        boxSizing: 'border-box',
                      }}
                    />
                  </View>
                  
                  <View className="flex-row justify-end space-x-3">
                    <TouchableOpacity 
                      onPress={cancelDateSelection}
                      className="px-4 py-2 bg-gray-200 rounded-lg"
                    >
                      <Text className="text-gray-700 font-medium">キャンセル</Text>
                    </TouchableOpacity>
                    <TouchableOpacity 
                      onPress={confirmDateSelection}
                      className="px-4 py-2 bg-blue-500 rounded-lg"
                    >
                      <Text className="text-white font-medium">完了</Text>
                    </TouchableOpacity>
                  </View>
                </TouchableOpacity>
              </TouchableOpacity>
            </Modal>
          ) : Platform.OS === 'ios' ? (
            <Modal
              visible={true}
              transparent={true}
              animationType="slide"
              onRequestClose={cancelDateSelection}
            >
              <TouchableOpacity 
                className="flex-1 justify-end bg-black/50"
                activeOpacity={1}
                onPress={cancelDateSelection}
              >
                <TouchableOpacity 
                  className="bg-white"
                  activeOpacity={1}
                  onPress={(e) => e.stopPropagation()}
                >
                  {/* iOS Date Picker Header */}
                  <View className="flex-row justify-between items-center p-4 border-b border-gray-200">
                    <TouchableOpacity onPress={cancelDateSelection}>
                      <Text className="text-blue-500 text-lg">キャンセル</Text>
                    </TouchableOpacity>
                    <Text className="text-lg font-semibold text-gray-800">
                      {showDatePicker === "start" ? "開始日を選択" : "終了日を選択"}
                    </Text>
                    <TouchableOpacity onPress={confirmDateSelection}>
                      <Text className="text-blue-500 text-lg font-semibold">完了</Text>
                    </TouchableOpacity>
                  </View>
                  
                  {/* iOS Date Picker */}
                  <View style={{ height: 200, backgroundColor: 'white' }}>
                    {DateTimePicker && (
                      <DateTimePicker
                        value={tempDate}
                        mode="date"
                        display="spinner"
                        onChange={handleDateChange}
                        style={{ 
                          backgroundColor: 'white',
                          height: 200,
                        }}
                      />
                    )}
                  </View>
                </TouchableOpacity>
              </TouchableOpacity>
            </Modal>
          ) : (
            // Android Date Picker
            DateTimePicker && (
              <DateTimePicker
                value={tempDate}
                mode="date"
                display="default"
                onChange={handleDateChange}
              />
            )
          )}
        </>
      )}

      {/* Status Selection Modal */}
      <Modal
        visible={showStatusModal}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setShowStatusModal(false)}
      >
        <TouchableOpacity
          className="flex-1 bg-black/50 justify-center items-center"
          activeOpacity={1}
          onPress={() => setShowStatusModal(false)}
        >
          <View className="bg-white rounded-lg m-4 p-4 min-w-[280px]">
            <Text className="text-lg font-bold text-gray-800 mb-4">ステータスを選択</Text>
            
            {statusOptions.map((option, index) => (
              <TouchableOpacity
                key={index}
                className="py-3 border-b border-gray-100 last:border-b-0"
                onPress={() => handleStatusSelect(option.value)}
              >
                <View className="flex-row justify-between items-center">
                  <Text className="text-gray-800">{option.label}</Text>
                  {filters.status === option.value && (
                    <Ionicons name="checkmark" size={20} color="#2563EB" />
                  )}
                </View>
              </TouchableOpacity>
            ))}
          </View>
        </TouchableOpacity>
      </Modal>
    </View>
  );
}