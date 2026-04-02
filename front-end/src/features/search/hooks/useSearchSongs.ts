import { useState, useCallback } from 'react'
import type { Song } from '@/features/search/types'
import { searchSongs } from '@/features/search/api/searchApi'

export const useSearchSongs = () => {
  const [songs, setSongs] = useState<Song[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [keyword, setKeyword] = useState('')
  const [hasSearched, setHasSearched] = useState(false)

  const search = useCallback(async (query: string, pageNum: number = 0) => {
    if (!query.trim()) return

    setLoading(true)
    setError(null)
    setKeyword(query)
    setHasSearched(true)
    try {
      const data = await searchSongs(query.trim(), pageNum)
      if (pageNum === 0) {
        setSongs(data.content)
      } else {
        setSongs(prev => [...prev, ...data.content])
      }
      setPage(data.page)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch {
      setError('검색에 실패했습니다.')
      if (pageNum === 0) setSongs([])
    } finally {
      setLoading(false)
    }
  }, [])

  const loadMore = useCallback(() => {
    if (page + 1 < totalPages && !loading) {
      search(keyword, page + 1)
    }
  }, [page, totalPages, loading, keyword, search])

  const hasMore = page + 1 < totalPages

  return { songs, loading, error, totalElements, hasMore, hasSearched, search, loadMore }
}
