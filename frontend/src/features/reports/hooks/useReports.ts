import { useState, useEffect, useCallback } from "react";
import { axiosInstance } from "../../../utils/axiosInstance";
import { components } from "../../../../types/api";
import { FilterOptions } from "../components/ReportFilter";

type DailyReportDto = components["schemas"]["DailyReportDto"];
type DailyReportListResponseDto = components["schemas"]["DailyReportListResponseDto"];

interface UseReportsState {
  reports: DailyReportDto[];
  loading: boolean;
  refreshing: boolean;
  loadingMore: boolean;
  error: string | null;
  hasMore: boolean;
  totalCount: number;
}

interface UseReportsReturn extends UseReportsState {
  fetchReports: (filters?: FilterOptions) => Promise<void>;
  refreshReports: () => Promise<void>;
  loadMoreReports: () => Promise<void>;
  clearError: () => void;
}

const DEFAULT_USER_ID = "default-user"; // TODO: 実際のユーザー認証実装後に動的に取得

export default function useReports(): UseReportsReturn {
  const [state, setState] = useState<UseReportsState>({
    reports: [],
    loading: false,
    refreshing: false,
    loadingMore: false,
    error: null,
    hasMore: true,
    totalCount: 0,
  });

  const [currentFilters, setCurrentFilters] = useState<FilterOptions>({});
  const [currentPage, setCurrentPage] = useState(0);
  const pageSize = 20;

  const buildQueryParams = useCallback((filters: FilterOptions = {}, page = 0) => {
    const params = new URLSearchParams();
    params.append("userId", DEFAULT_USER_ID);
    
    if (filters.startDate) {
      params.append("startDate", filters.startDate);
    }
    
    if (filters.endDate) {
      params.append("endDate", filters.endDate);
    }
    

    // Note: Search functionality would need backend support for text search
    // This is a placeholder for when backend implements search
    if (filters.searchText && filters.searchText.trim()) {
      params.append("search", filters.searchText.trim());
    }

    return params.toString();
  }, []);

  const fetchReports = useCallback(async (filters: FilterOptions = {}, isRefresh = false, isLoadMore = false) => {
    try {
      const page = isLoadMore ? currentPage + 1 : 0;
      
      setState(prev => ({
        ...prev,
        loading: !isRefresh && !isLoadMore && prev.reports.length === 0,
        refreshing: isRefresh,
        loadingMore: isLoadMore,
        error: null,
      }));

      const queryString = buildQueryParams(filters, page);
      const response = await axiosInstance.get<DailyReportListResponseDto>(`/api/reports?${queryString}`);
      
      const data = response.data;
      const newReports = data.reports || [];
      
      setState(prev => ({
        ...prev,
        reports: isLoadMore ? [...prev.reports, ...newReports] : newReports,
        loading: false,
        refreshing: false,
        loadingMore: false,
        totalCount: data.totalCount || 0,
        hasMore: newReports.length === pageSize, // Assume more data exists if we got a full page
        error: null,
      }));

      if (!isLoadMore) {
        setCurrentFilters(filters);
        setCurrentPage(0);
      } else {
        setCurrentPage(page);
      }

    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 
                          error.message || 
                          "日報の取得に失敗しました";
      
      setState(prev => ({
        ...prev,
        loading: false,
        refreshing: false,
        loadingMore: false,
        error: errorMessage,
      }));
    }
  }, [buildQueryParams, currentPage]);

  const refreshReports = useCallback(async () => {
    await fetchReports(currentFilters, true, false);
  }, [fetchReports, currentFilters]);

  const loadMoreReports = useCallback(async () => {
    if (state.hasMore && !state.loadingMore && !state.loading) {
      await fetchReports(currentFilters, false, true);
    }
  }, [fetchReports, currentFilters, state.hasMore, state.loadingMore, state.loading]);

  const clearError = useCallback(() => {
    setState(prev => ({
      ...prev,
      error: null,
    }));
  }, []);

  // Initial load
  useEffect(() => {
    fetchReports({});
  }, [fetchReports]);

  return {
    ...state,
    fetchReports: useCallback((filters?: FilterOptions) => fetchReports(filters, false, false), [fetchReports]),
    refreshReports,
    loadMoreReports,
    clearError,
  };
}