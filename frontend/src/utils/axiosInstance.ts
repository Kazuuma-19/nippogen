import axios from 'axios'

export const axiosInstance = axios.create({
  baseURL: process.env.EXPO_PUBLIC_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// Request interceptor for adding user ID
axiosInstance.interceptors.request.use(
  (config) => {
    // TODO: 実際のユーザーIDを取得して設定
    config.headers['X-User-Id'] = 'mock-user-id'
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor for error handling
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)