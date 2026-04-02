import { useState, useCallback } from 'react'
import { fetchPlaylistSongs } from '@/features/playlist/api/playlistApi'
import type { PlaylistSong } from '@/features/playlist/types'

export const usePlaylistSongs = () => {
  const [songs, setSongs] = useState<PlaylistSong[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const loadSongs = useCallback(async (playlistId: number) => {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchPlaylistSongs(playlistId)
      setSongs(data)
    } catch {
      setError('곡 목록을 불러오는데 실패했습니다.')
      setSongs([])
    } finally {
      setLoading(false)
    }
  }, [])

  return { songs, loading, error, loadSongs }
}
