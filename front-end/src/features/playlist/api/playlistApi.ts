import apiClient from '@/api/client'
import type { Playlist } from '@/features/playlist/types'

export const fetchPlaylists = async (): Promise<Playlist[]> => {
  const { data } = await apiClient.get<Playlist[]>('/api/playlists')
  return data
}

export interface CreatePlaylistParams {
  name: string
  description?: string
  isPublic: boolean
}

export const createPlaylist = async (params: CreatePlaylistParams): Promise<Playlist> => {
  const { data } = await apiClient.post<Playlist>('/api/playlists', params)
  return data
}
