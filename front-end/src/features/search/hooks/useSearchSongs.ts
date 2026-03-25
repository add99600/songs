import { useState, useCallback } from 'react'
import type { Song } from '@/features/search/types'
import { searchSongs } from '@/features/search/api/searchApi'

export const useSearchSongs = () => {
  const [results, setResults] = useState<Song[]>([])
  const [hasSearched, setHasSearched] = useState(false)
  const [loading, setLoading] = useState(false)

  const search = useCallback(async (keyword: string) => {
    if (!keyword.trim()) return

    setLoading(true)
    setHasSearched(true)
    try {
      const data = await searchSongs(keyword.trim())
      setResults(data)
    } catch {
      setResults([])
    } finally {
      setLoading(false)
    }
  }, [])

  return { results, hasSearched, loading, search }
}
