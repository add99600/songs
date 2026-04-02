import apiClient from '@/api/client'
import type { PagedSongResponse } from '@/features/search/types'

export const searchSongs = async (
  keyword: string,
  page: number = 0,
  size: number = 10
): Promise<PagedSongResponse> => {
  const { data } = await apiClient.get<PagedSongResponse>('/api/songs', {
    params: { keyword, page, size },
  })
  return data
}
