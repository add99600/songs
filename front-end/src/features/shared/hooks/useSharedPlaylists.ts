import { useEffect, useState, useCallback } from 'react'
import type { SharedPlaylist } from '@/features/shared/types'
import { fetchSharedPlaylists } from '@/features/shared/api/sharedApi'

export const useSharedPlaylists = () => {
  const [sharedList, setSharedList] = useState<SharedPlaylist[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refetch = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await fetchSharedPlaylists()
      setSharedList(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : '공유 폴더를 불러오는 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    refetch()
  }, [refetch])

  return { sharedList, loading, error, refetch }
}
