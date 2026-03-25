import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import type { Song } from '@/features/search/types'

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
  return (
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
      <Button
        variant="ghost"
        size="icon"
        className="shrink-0 opacity-0 group-hover:opacity-100 transition-opacity"
        aria-label="플레이리스트에 추가"
      >
        <Plus className="size-4" />
      </Button>
    </div>
  )
}
