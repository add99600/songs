import { Eye, Pencil } from 'lucide-react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import type { SharedPlaylist } from '@/features/shared/types'

const avatarColors = [
  'bg-violet-100 text-violet-700 dark:bg-violet-900/50 dark:text-violet-300',
  'bg-sky-100 text-sky-700 dark:bg-sky-900/50 dark:text-sky-300',
  'bg-rose-100 text-rose-700 dark:bg-rose-900/50 dark:text-rose-300',
  'bg-amber-100 text-amber-700 dark:bg-amber-900/50 dark:text-amber-300',
  'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/50 dark:text-emerald-300',
]

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
}

interface SharedPlaylistCardProps {
  item: SharedPlaylist
  index: number
}

export const SharedPlaylistCard = ({ item, index }: SharedPlaylistCardProps) => {
  return (
    <div className="group rounded-xl border bg-card p-5 flex items-center gap-4 hover:shadow-md hover:border-primary/20 transition-all duration-200 cursor-pointer">
      <Avatar className={`size-11 ${avatarColors[index % avatarColors.length]}`}>
        <AvatarFallback className={avatarColors[index % avatarColors.length]}>
          {item.ownerNickname[0]}
        </AvatarFallback>
      </Avatar>
      <div className="flex-1 min-w-0">
        <p className="font-semibold text-sm truncate">{item.playlistName}</p>
        <p className="text-xs text-muted-foreground mt-0.5">
          {item.ownerNickname} · {formatDate(item.sharedAt)}
        </p>
      </div>
      <Badge
        variant="outline"
        className={`gap-1 shrink-0 ${
          item.permission === 'EDIT'
            ? 'border-blue-200 text-blue-700 bg-blue-50 dark:border-blue-800 dark:text-blue-300 dark:bg-blue-900/30'
            : ''
        }`}
      >
        {item.permission === 'EDIT' ? (
          <><Pencil className="size-3" /> 편집</>
        ) : (
          <><Eye className="size-3" /> 읽기</>
        )}
      </Badge>
    </div>
  )
}
