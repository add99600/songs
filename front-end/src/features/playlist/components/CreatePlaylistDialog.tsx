import { useState } from 'react'
import { FolderPlus, Globe, Lock, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Switch } from '@/components/ui/switch'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { createPlaylist } from '@/features/playlist/api/playlistApi'

interface CreatePlaylistDialogProps {
  onCreated: () => void
  trigger?: React.ReactNode
}

export const CreatePlaylistDialog = ({ onCreated, trigger }: CreatePlaylistDialogProps) => {
  const [open, setOpen] = useState(false)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [isPublic, setIsPublic] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const reset = () => {
    setName('')
    setDescription('')
    setIsPublic(false)
    setError(null)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const trimmedName = name.trim()
    if (!trimmedName) {
      setError('폴더 이름을 입력해주세요.')
      return
    }
    if (trimmedName.length > 100) {
      setError('폴더 이름은 100자 이내여야 합니다.')
      return
    }

    setSubmitting(true)
    setError(null)
    try {
      await createPlaylist({
        name: trimmedName,
        description: description.trim() || undefined,
        isPublic,
      })
      setOpen(false)
      reset()
      onCreated()
    } catch {
      setError('폴더 생성에 실패했습니다. 다시 시도해주세요.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <span onClick={() => setOpen(true)}>
        {trigger ?? (
          <Button className="gap-2 rounded-xl shadow-sm">
            <FolderPlus className="size-4" />
            새 폴더
          </Button>
        )}
      </span>

      <Dialog open={open} onOpenChange={(v) => { setOpen(v); if (!v) reset() }}>
        <DialogContent className="sm:max-w-md">
          <form onSubmit={handleSubmit}>
            <DialogHeader>
              <DialogTitle>새 폴더 만들기</DialogTitle>
              <DialogDescription>
                즐겨 부르는 곡들을 모아둘 폴더를 만들어보세요.
              </DialogDescription>
            </DialogHeader>

            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="playlist-name">
                  폴더 이름 <span className="text-destructive">*</span>
                </Label>
                <Input
                  id="playlist-name"
                  placeholder="예: 발라드 모음, 회식 필수곡"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  maxLength={100}
                  autoFocus
                />
                <p className="text-xs text-muted-foreground text-right">
                  {name.length}/100
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="playlist-desc">설명 (선택)</Label>
                <Input
                  id="playlist-desc"
                  placeholder="이 폴더에 대한 간단한 설명"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </div>

              <div className="flex items-center justify-between rounded-lg border p-3">
                <div className="flex items-center gap-2">
                  {isPublic ? (
                    <Globe className="size-4 text-primary" />
                  ) : (
                    <Lock className="size-4 text-muted-foreground" />
                  )}
                  <div>
                    <p className="text-sm font-medium">
                      {isPublic ? '공개' : '비공개'}
                    </p>
                    <p className="text-xs text-muted-foreground">
                      {isPublic
                        ? '다른 사용자가 이 폴더를 볼 수 있어요'
                        : '나만 볼 수 있는 폴더예요'}
                    </p>
                  </div>
                </div>
                <Switch
                  checked={isPublic}
                  onCheckedChange={setIsPublic}
                  aria-label="공개 여부"
                />
              </div>

              {error && (
                <p className="text-sm text-destructive">{error}</p>
              )}
            </div>

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setOpen(false)}
                disabled={submitting}
              >
                취소
              </Button>
              <Button type="submit" disabled={submitting || !name.trim()} className="gap-2">
                {submitting ? (
                  <Loader2 className="size-4 animate-spin" />
                ) : (
                  <FolderPlus className="size-4" />
                )}
                만들기
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  )
}
