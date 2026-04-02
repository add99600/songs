import apiClient from '@/api/client'

export interface LyricsResponse {
  songNo: string
  brand: string
  lyrics: string
  source: string
}

export const fetchLyrics = async (
  songNo: string,
  brand: string = 'tj',
  title: string = '',
  singer: string = ''
): Promise<LyricsResponse> => {
  const { data } = await apiClient.get<LyricsResponse>(`/api/songs/${songNo}/lyrics`, {
    params: { brand, title, singer },
  })
  return data
}
