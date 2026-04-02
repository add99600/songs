import { useState, useCallback } from 'react'
import { fetchLyrics, type LyricsResponse } from '@/features/search/api/lyricsApi'

export const useLyrics = () => {
  const [lyrics, setLyrics] = useState<LyricsResponse | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const loadLyrics = useCallback(async (songNo: string, brand: string = 'tj', title: string = '', singer: string = '') => {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchLyrics(songNo, brand, title, singer)
      setLyrics(data)
    } catch (e) {
      setError('가사를 불러오는데 실패했습니다.')
      setLyrics(null)
    } finally {
      setLoading(false)
    }
  }, [])

  const reset = useCallback(() => {
    setLyrics(null)
    setError(null)
    setLoading(false)
  }, [])

  return { lyrics, loading, error, loadLyrics, reset }
}
