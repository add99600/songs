import { useNavigate } from 'react-router-dom'
import { Globe, Lock, MoreHorizontal } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import type { Playlist } from '@/features/playlist/types'

const cardAccents = [
  'from-violet-500 to-purple-500',
  'from-blue-500 to-cyan-500',
  'from-pink-500 to-rose-500',
  'from-amber-500 to-orange-500',
  'from-emerald-500 to-teal-500',
]

interface PlaylistCardProps {
  playlist: Playlist
  index: number
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
}

export const PlaylistCard = ({ playlist, index }: PlaylistCardProps) => {
  const navigate = useNavigate()

  const handleClick = () => {
    navigate(`/my-folders/${playlist.id}`, { state: { playlist } })
  }

  return (
    <div
      onClick={handleClick}
      className="group rounded-xl border bg-card overflow-hidden hover:shadow-lg hover:-translate-y-1 transition-all duration-300 cursor-pointer"
    >
      <div className={`h-1.5 bg-gradient-to-r ${cardAccents[index % cardAccents.length]}`} />
      <div className="p-5">
        <div className="flex items-start justify-between">
          <div className="min-w-0 flex-1">
            <h3 className="font-semibold text-base truncate">{playlist.name}</h3>
            {playlist.description && (
              <p className="text-sm text-muted-foreground mt-1 line-clamp-2">
                {playlist.description}
              </p>
            )}
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="size-8 shrink-0 opacity-0 group-hover:opacity-100 transition-opacity -mt-1 -mr-2"
          >
            <MoreHorizontal className="size-4" />
          </Button>
        </div>
        <div className="flex items-center gap-2 mt-3">
          <Badge variant="outline" className="text-[10px] gap-1 px-1.5 py-0">
            {playlist.isPublic ? (
              <><Globe className="size-2.5" /> 공개</>
            ) : (
              <><Lock className="size-2.5" /> 비공개</>
            )}
          </Badge>
          <span className="text-[10px] text-muted-foreground">
            {formatDate(playlist.createdAt)}
          </span>
        </div>
      </div>
    </div>
  )
}
