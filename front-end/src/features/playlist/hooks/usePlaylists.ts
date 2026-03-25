import { useEffect, useState, useCallback } from 'react'
import type { Playlist } from '@/features/playlist/types'
import { fetchPlaylists } from '@/features/playlist/api/playlistApi'

export const usePlaylists = () => {
  const [folders, setFolders] = useState<Playlist[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refetch = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await fetchPlaylists()
      setFolders(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : '폴더를 불러오는 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    refetch()
  }, [refetch])

  return { folders, loading, error, refetch }
}
