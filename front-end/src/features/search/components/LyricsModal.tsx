import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Loader2, Music2 } from 'lucide-react'

interface LyricsModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  songTitle: string
  singer: string
  lyrics: string | null
  loading: boolean
  error: string | null
}

export const LyricsModal = ({
  open,
  onOpenChange,
  songTitle,
  singer,
  lyrics,
  loading,
  error,
}: LyricsModalProps) => {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg max-h-[80vh] overflow-hidden flex flex-col">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Music2 className="size-5 text-primary" />
            <div className="min-w-0">
              <p className="truncate">{songTitle}</p>
              <p className="text-sm font-normal text-muted-foreground truncate">{singer}</p>
            </div>
          </DialogTitle>
        </DialogHeader>

        <div className="flex-1 overflow-y-auto mt-4">
          {loading && (
            <div className="flex flex-col items-center justify-center py-12 text-muted-foreground">
              <Loader2 className="size-8 animate-spin mb-3" />
              <p className="text-sm">가사를 불러오는 중...</p>
            </div>
          )}

          {error && (
            <div className="flex flex-col items-center justify-center py-12 text-muted-foreground">
              <p className="text-sm text-destructive">{error}</p>
            </div>
          )}

          {!loading && !error && lyrics !== null && lyrics.length > 0 && (
            <pre className="whitespace-pre-wrap font-sans text-sm leading-relaxed text-foreground">
              {lyrics}
            </pre>
          )}

          {!loading && !error && (lyrics === null || lyrics.length === 0) && (
            <div className="flex flex-col items-center justify-center py-12 text-muted-foreground">
              <Music2 className="size-10 mb-3 opacity-30" />
              <p className="text-sm">가사를 찾을 수 없습니다.</p>
              <p className="text-xs mt-1">가사 소스가 연동되면 자동으로 표시됩니다.</p>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  )
}
