import { axiosInstance } from './axiosInstance'
import type { components } from '@/types/api'

// GitHub Credentials API
export const githubCredentialsApi = {
  findAllByUserId: () => 
    axiosInstance.get<components['schemas']['GitHubCredentialResponseDto'][]>('/api/credentials/github/all'),
  
  findByUserId: () => 
    axiosInstance.get<components['schemas']['GitHubCredentialResponseDto']>('/api/credentials/github'),
  
  create: (data: components['schemas']['GitHubCredentialCreateRequestDto']) => 
    axiosInstance.post<components['schemas']['GitHubCredentialResponseDto']>('/api/credentials/github', data),
  
  delete: (id: string) => 
    axiosInstance.delete(`/api/credentials/github/${id}`),
  
  test: (owner: string, repo: string) => 
    axiosInstance.get('/api/credentials/github/test', { params: { owner, repo } })
}

// Toggl Credentials API
export const togglCredentialsApi = {
  findAllByUserId: () => 
    axiosInstance.get<components['schemas']['TogglCredentialResponseDto'][]>('/api/credentials/toggl/all'),
  
  findByUserId: () => 
    axiosInstance.get<components['schemas']['TogglCredentialResponseDto']>('/api/credentials/toggl'),
  
  create: (data: components['schemas']['TogglCredentialCreateRequestDto']) => 
    axiosInstance.post<components['schemas']['TogglCredentialResponseDto']>('/api/credentials/toggl', data),
  
  delete: (id: string) => 
    axiosInstance.delete(`/api/credentials/toggl/${id}`),
  
  test: () => 
    axiosInstance.get('/api/credentials/toggl/test')
}

// Notion Credentials API
export const notionCredentialsApi = {
  findAllByUserId: () => 
    axiosInstance.get<components['schemas']['NotionCredentialResponseDto'][]>('/api/credentials/notion/all'),
  
  findByUserId: () => 
    axiosInstance.get<components['schemas']['NotionCredentialResponseDto']>('/api/credentials/notion'),
  
  create: (data: components['schemas']['NotionCredentialCreateRequestDto']) => 
    axiosInstance.post<components['schemas']['NotionCredentialResponseDto']>('/api/credentials/notion', data),
  
  delete: (id: string) => 
    axiosInstance.delete(`/api/credentials/notion/${id}`),
  
  test: () => 
    axiosInstance.get('/api/credentials/notion/test')
}

// Combined credentials API
export const credentialsApi = {
  github: githubCredentialsApi,
  toggl: togglCredentialsApi,
  notion: notionCredentialsApi
}