import apiClient from '@/api/client'
import type { SharedPlaylist } from '@/features/shared/types'

export const fetchSharedPlaylists = async (): Promise<SharedPlaylist[]> => {
  const { data } = await apiClient.get<SharedPlaylist[]>('/api/playlists/shared')
  return data
}
