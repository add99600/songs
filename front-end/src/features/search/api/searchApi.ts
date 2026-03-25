import apiClient from '@/api/client'
import type { Song } from '@/features/search/types'

export const searchSongs = async (keyword: string): Promise<Song[]> => {
  const { data } = await apiClient.get<Song[]>('/api/songs', {
    params: { keyword }
  })
  return data
}
