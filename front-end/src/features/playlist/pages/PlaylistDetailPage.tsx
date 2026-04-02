import { useEffect } from 'react'
import { useParams, useNavigate, useLocation } from 'react-router-dom'
import { ArrowLeft, Music2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { usePlaylistSongs } from '@/features/playlist/hooks/usePlaylistSongs'
import type { Playlist } from '@/features/playlist/types'

const PlaylistDetailPage = () => {
  const { playlistId } = useParams<{ playlistId: string }>()
  const navigate = useNavigate()
  const location = useLocation()
  const playlist = location.state?.playlist as Playlist | undefined

  const { songs, loading, error, loadSongs } = usePlaylistSongs()

  useEffect(() => {
    if (playlistId) {
      loadSongs(Number(playlistId))
    }
  }, [playlistId, loadSongs])

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate('/my-folders')}
          aria-label="뒤로 가기"
        >
          <ArrowLeft className="size-5" />
        </Button>
        <div className="min-w-0">
          <h1 className="text-2xl font-bold truncate">
            {playlist?.name ?? '플레이리스트'}
          </h1>
          {playlist?.description && (
            <p className="text-sm text-muted-foreground truncate mt-0.5">
              {playlist.description}
            </p>
          )}
        </div>
      </div>

      {/* Loading */}
      {loading && (
        <div className="space-y-3">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="flex items-center gap-4 p-4 rounded-xl border">
              <Skeleton className="w-10 h-10 rounded-lg" />
              <div className="flex-1 space-y-2">
                <Skeleton className="h-4 w-48" />
                <Skeleton className="h-3 w-32" />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Error */}
      {error && (
        <div className="text-center py-12 text-destructive">
          <p>{error}</p>
          <Button
            variant="outline"
            className="mt-4"
            onClick={() => playlistId && loadSongs(Number(playlistId))}
          >
            다시 시도
          </Button>
        </div>
      )}

      {/* Empty */}
      {!loading && !error && songs.length === 0 && (
        <div className="text-center py-16 text-muted-foreground">
          <Music2 className="size-16 mx-auto mb-4 opacity-20" />
          <p className="text-lg font-medium">아직 추가된 곡이 없습니다</p>
          <p className="text-sm mt-1">노래 검색에서 곡을 추가해보세요</p>
          <Button
            variant="outline"
            className="mt-4"
            onClick={() => navigate('/search')}
          >
            노래 검색하러 가기
          </Button>
        </div>
      )}

      {/* Song list */}
      {!loading && !error && songs.length > 0 && (
        <div className="space-y-2">
          <p className="text-sm text-muted-foreground">{songs.length}곡</p>
          <div className="space-y-2">
            {songs.map((song, index) => (
              <div
                key={song.id}
                className="flex items-center gap-4 p-4 rounded-xl border bg-card hover:shadow-md hover:border-primary/20 transition-all duration-200"
              >
                <span className="w-8 text-center text-sm text-muted-foreground font-mono">
                  {index + 1}
                </span>
                <div className="w-12 h-12 shrink-0 rounded-lg border flex flex-col items-center justify-center bg-muted/50">
                  <span className="text-[10px] font-bold leading-none text-muted-foreground">
                    {song.songBrand?.toUpperCase() || 'TJ'}
                  </span>
                  <span className="text-xs font-mono font-bold leading-tight">
                    {song.songNo || '-'}
                  </span>
                </div>
                <div className="min-w-0 flex-1">
                  <p className="font-semibold text-sm truncate">
                    {song.songTitle || song.songId}
                  </p>
                  <p className="text-xs text-muted-foreground truncate mt-0.5">
                    {song.songSinger || ''}
                  </p>
                </div>
                <span className="text-xs text-muted-foreground shrink-0">
                  {new Date(song.addedAt).toLocaleDateString('ko-KR', {
                    month: 'short',
                    day: 'numeric',
                  })}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default PlaylistDetailPage
