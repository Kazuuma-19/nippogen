import { axiosInstance } from './axiosInstance'
import type { components } from '@/types/api'

// GitHub Credentials API
export const githubCredentialsApi = {
  findAllByUserId: () => 
    axiosInstance.get<components['schemas']['GitHubCredentialResponseDto'][]>('/api/credentials/github/all'),
  
  findByUserId: () => 
    axiosInstance.get<components['schemas']['GitHubCredentialResponseDto']>('/api/credentials/github'),
  
  findById: (id: string) => 
    axiosInstance.get<components['schemas']['GitHubCredentialResponseDto']>(`/api/credentials/github/${id}`),
  
  create: (data: components['schemas']['GitHubCredentialCreateRequestDto']) => 
    axiosInstance.post<components['schemas']['GitHubCredentialResponseDto']>('/api/credentials/github', data),
  
  update: (id: string, data: components['schemas']['GitHubCredentialUpdateRequestDto']) => 
    axiosInstance.put<components['schemas']['GitHubCredentialResponseDto']>(`/api/credentials/github/${id}`, data),
  
  delete: (id: string) => 
    axiosInstance.delete(`/api/credentials/github/${id}`),
  
  exists: () => 
    axiosInstance.get<boolean>('/api/credentials/github/exists'),
  
  test: (owner: string, repo: string) => 
    axiosInstance.get('/api/external/github/test', { params: { owner, repo } })
}

// Toggle Credentials API
export const toggleCredentialsApi = {
  findAllByUserId: () => 
    axiosInstance.get<components['schemas']['ToggleCredentialResponseDto'][]>('/api/credentials/toggl/all'),
  
  findByUserId: () => 
    axiosInstance.get<components['schemas']['ToggleCredentialResponseDto']>('/api/credentials/toggl'),
  
  findById: (id: string) => 
    axiosInstance.get<components['schemas']['ToggleCredentialResponseDto']>(`/api/credentials/toggl/${id}`),
  
  create: (data: components['schemas']['ToggleCredentialCreateRequestDto']) => 
    axiosInstance.post<components['schemas']['ToggleCredentialResponseDto']>('/api/credentials/toggl', data),
  
  update: (id: string, data: components['schemas']['ToggleCredentialUpdateRequestDto']) => 
    axiosInstance.put<components['schemas']['ToggleCredentialResponseDto']>(`/api/credentials/toggl/${id}`, data),
  
  delete: (id: string) => 
    axiosInstance.delete(`/api/credentials/toggl/${id}`),
  
  exists: () => 
    axiosInstance.get<boolean>('/api/credentials/toggl/exists'),
  
  test: () => 
    axiosInstance.get('/api/external/toggl/test')
}

// Notion Credentials API
export const notionCredentialsApi = {
  findAllByUserId: () => 
    axiosInstance.get<components['schemas']['NotionCredentialResponseDto'][]>('/api/credentials/notion/all'),
  
  findByUserId: () => 
    axiosInstance.get<components['schemas']['NotionCredentialResponseDto']>('/api/credentials/notion'),
  
  findById: (id: string) => 
    axiosInstance.get<components['schemas']['NotionCredentialResponseDto']>(`/api/credentials/notion/${id}`),
  
  create: (data: components['schemas']['NotionCredentialCreateRequestDto']) => 
    axiosInstance.post<components['schemas']['NotionCredentialResponseDto']>('/api/credentials/notion', data),
  
  update: (id: string, data: components['schemas']['NotionCredentialUpdateRequestDto']) => 
    axiosInstance.put<components['schemas']['NotionCredentialResponseDto']>(`/api/credentials/notion/${id}`, data),
  
  delete: (id: string) => 
    axiosInstance.delete(`/api/credentials/notion/${id}`),
  
  exists: () => 
    axiosInstance.get<boolean>('/api/credentials/notion/exists'),
  
  test: () => 
    axiosInstance.get('/api/external/notion/test')
}

// Combined credentials API
export const credentialsApi = {
  github: githubCredentialsApi,
  toggle: toggleCredentialsApi,
  notion: notionCredentialsApi
}