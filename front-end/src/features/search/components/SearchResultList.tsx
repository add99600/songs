import { Music, Mic2 } from 'lucide-react'
import { SongCard } from '@/features/search/components/SongCard'
import type { Song } from '@/features/search/types'

interface SearchResultListProps {
  results: Song[]
  hasSearched: boolean
  loading: boolean
}

export const SearchResultList = ({ results, hasSearched, loading }: SearchResultListProps) => {
  if (!hasSearched) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-center">
        <div className="w-20 h-20 rounded-2xl bg-primary/5 flex items-center justify-center mb-5">
          <Mic2 className="size-10 text-primary/30" />
        </div>
        <h3 className="font-semibold text-lg">노래를 검색해보세요</h3>
        <p className="text-sm text-muted-foreground mt-1 max-w-xs">
          곡 제목이나 가수명을 입력하면<br />노래방 번호를 바로 확인할 수 있어요
        </p>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="space-y-3 pt-4">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="rounded-xl border bg-card p-4 animate-pulse">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-muted" />
              <div className="flex-1 space-y-2">
                <div className="h-4 w-40 bg-muted rounded" />
                <div className="h-3 w-28 bg-muted rounded" />
              </div>
            </div>
          </div>
        ))}
      </div>
    )
  }

  if (results.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-center">
        <div className="w-20 h-20 rounded-2xl bg-muted flex items-center justify-center mb-5">
          <Music className="size-10 text-muted-foreground/40" />
        </div>
        <h3 className="font-semibold text-lg">검색 결과가 없습니다</h3>
        <p className="text-sm text-muted-foreground mt-1">
          다른 키워드로 다시 검색해보세요
        </p>
      </div>
    )
  }

  return (
    <div className="space-y-2 pt-4">
      <p className="text-sm text-muted-foreground mb-3">
        {results.length}개의 검색 결과
      </p>
      {results.map((song, idx) => (
        <SongCard key={song.id ?? idx} song={song} />
      ))}
    </div>
  )
}
