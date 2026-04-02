import { useState } from 'react'
import { Plus, FileText, Check, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { fetchPlaylists, addSongToPlaylist } from '@/features/playlist/api/playlistApi'
import type { Playlist } from '@/features/playlist/types'
import type { Song } from '@/features/search/types'
import { LyricsModal } from './LyricsModal'
import { useLyrics } from '@/features/search/hooks/useLyrics'

const getBrandColor = (brand: string) => {
  switch (brand?.toUpperCase()) {
    case 'TJ':
      return 'bg-blue-100 text-blue-700 border-blue-200 dark:bg-blue-900/50 dark:text-blue-300 dark:border-blue-800'
    case 'KY':
      return 'bg-orange-100 text-orange-700 border-orange-200 dark:bg-orange-900/50 dark:text-orange-300 dark:border-orange-800'
    default:
      return 'bg-muted text-muted-foreground'
  }
}

interface SongCardProps {
  song: Song
}

export const SongCard = ({ song }: SongCardProps) => {
  const [lyricsOpen, setLyricsOpen] = useState(false)
  const { lyrics, loading, error, loadLyrics, reset } = useLyrics()

  const [playlistPopoverOpen, setPlaylistPopoverOpen] = useState(false)
  const [playlists, setPlaylists] = useState<Playlist[]>([])
  const [loadingPlaylists, setLoadingPlaylists] = useState(false)
  const [addingTo, setAddingTo] = useState<number | null>(null)
  const [addedTo, setAddedTo] = useState<Set<number>>(new Set())

  const handleOpenPlaylistPopover = async (open: boolean) => {
    setPlaylistPopoverOpen(open)
    if (open && playlists.length === 0) {
      setLoadingPlaylists(true)
      try {
        const data = await fetchPlaylists()
        setPlaylists(data)
      } catch {
        // ignore
      } finally {
        setLoadingPlaylists(false)
      }
    }
  }

  const handleAddToPlaylist = async (playlistId: number) => {
    setAddingTo(playlistId)
    try {
      await addSongToPlaylist(playlistId, {
        songId: song.no || song.id || '',
        songNo: song.no,
        title: song.title,
        singer: song.singer,
        brand: song.brand || 'tj',
      })
      setAddedTo(prev => new Set(prev).add(playlistId))
    } catch {
      // ignore - could be a duplicate song
    } finally {
      setAddingTo(null)
    }
  }

  const handleLyricsClick = () => {
    setLyricsOpen(true)
    loadLyrics(song.no, song.brand || 'tj', song.title, song.singer)
  }

  const handleOpenChange = (open: boolean) => {
    setLyricsOpen(open)
    if (!open) {
      reset()
    }
  }

  return (
    <>
      <div className="group flex items-center justify-between rounded-xl border bg-card p-4 hover:shadow-md hover:border-primary/20 transition-all duration-200">
        <div className="flex items-center gap-4 min-w-0">
          <div className={`w-12 h-12 shrink-0 rounded-lg border flex flex-col items-center justify-center ${getBrandColor(song.brand)}`}>
            <span className="text-[10px] font-bold leading-none">{song.brand || 'TJ'}</span>
            <span className="text-xs font-mono font-bold leading-tight">{song.no}</span>
          </div>
          <div className="min-w-0">
            <p className="font-semibold text-sm truncate">{song.title}</p>
            <p className="text-sm text-muted-foreground truncate">{song.singer}</p>
          </div>
        </div>
        <div className="flex items-center gap-1 shrink-0">
          <Button
            variant="ghost"
            size="icon"
            className="opacity-0 group-hover:opacity-100 transition-opacity"
            aria-label="가사 보기"
            onClick={handleLyricsClick}
          >
            <FileText className="size-4" />
          </Button>
          <Popover open={playlistPopoverOpen} onOpenChange={handleOpenPlaylistPopover}>
            <PopoverTrigger
              render={
                <Button
                  variant="ghost"
                  size="icon"
                  className="opacity-0 group-hover:opacity-100 transition-opacity"
                  aria-label="플레이리스트에 추가"
                />
              }
            >
              <Plus className="size-4" />
            </PopoverTrigger>
            <PopoverContent className="w-56 p-2" align="end">
              <p className="text-xs font-medium text-muted-foreground px-2 py-1">플레이리스트에 추가</p>
              {loadingPlaylists ? (
                <div className="flex justify-center py-4">
                  <Loader2 className="size-4 animate-spin text-muted-foreground" />
                </div>
              ) : playlists.length === 0 ? (
                <p className="text-xs text-muted-foreground text-center py-4">플레이리스트가 없습니다</p>
              ) : (
                <div className="space-y-0.5 max-h-48 overflow-y-auto">
                  {playlists.map(pl => (
                    <button
                      key={pl.id}
                      onClick={() => handleAddToPlaylist(pl.id)}
                      disabled={addingTo === pl.id || addedTo.has(pl.id)}
                      className="w-full flex items-center justify-between px-2 py-1.5 text-sm rounded-md hover:bg-accent disabled:opacity-50 text-left"
                    >
                      <span className="truncate">{pl.name}</span>
                      {addedTo.has(pl.id) ? (
                        <Check className="size-3.5 text-green-500 shrink-0" />
                      ) : addingTo === pl.id ? (
                        <Loader2 className="size-3.5 animate-spin shrink-0" />
                      ) : null}
                    </button>
                  ))}
                </div>
              )}
            </PopoverContent>
          </Popover>
        </div>
      </div>

      <LyricsModal
        open={lyricsOpen}
        onOpenChange={handleOpenChange}
        songTitle={song.title}
        singer={song.singer}
        lyrics={lyrics?.lyrics ?? null}
        loading={loading}
        error={error}
      />
    </>
  )
}
