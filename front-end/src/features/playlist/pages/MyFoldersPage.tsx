import { FolderPlus, Music, RefreshCw } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { usePlaylists } from '@/features/playlist/hooks/usePlaylists'
import { PlaylistCard } from '@/features/playlist/components/PlaylistCard'
import { CreatePlaylistDialog } from '@/features/playlist/components/CreatePlaylistDialog'

const MyFoldersPage = () => {
  const { folders, loading, error, refetch } = usePlaylists()

  return (
    <div className="flex flex-col">
      <div className="bg-gradient-to-b from-primary/5 to-transparent pt-8 pb-6 px-4 sm:px-6">
        <div className="container max-w-4xl mx-auto flex items-end justify-between">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">내 애창곡</h1>
            <p className="text-muted-foreground mt-1">즐겨 부르는 곡들을 폴더별로 정리하세요</p>
          </div>
          <CreatePlaylistDialog onCreated={refetch} />
        </div>
      </div>

      <div className="container max-w-4xl mx-auto px-4 sm:px-6 pb-8">
        {loading && (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 pt-2">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="rounded-xl border bg-card overflow-hidden">
                <div className="h-1.5 bg-muted" />
                <div className="p-5 space-y-3">
                  <Skeleton className="h-5 w-32" />
                  <Skeleton className="h-4 w-48" />
                  <Skeleton className="h-3 w-20" />
                </div>
              </div>
            ))}
          </div>
        )}

        {error && (
          <div className="flex flex-col items-center justify-center py-20 gap-4">
            <p className="text-destructive">{error}</p>
            <Button variant="outline" onClick={refetch} className="gap-2">
              <RefreshCw className="size-4" />
              다시 시도
            </Button>
          </div>
        )}

        {!loading && !error && (
          <>
            {folders.length > 0 ? (
              <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 pt-2">
                {folders.map((folder, idx) => (
                  <PlaylistCard key={folder.id} playlist={folder} index={idx} />
                ))}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-20 text-center">
                <div className="w-24 h-24 rounded-3xl bg-primary/5 flex items-center justify-center mb-6">
                  <Music className="size-12 text-primary/25" />
                </div>
                <h3 className="font-semibold text-lg">아직 폴더가 없어요</h3>
                <p className="text-sm text-muted-foreground mt-1 mb-6 max-w-xs">
                  검색에서 좋아하는 곡을 찾아 폴더에 저장해보세요
                </p>
                <CreatePlaylistDialog
                  onCreated={refetch}
                  trigger={
                    <Button className="gap-2 rounded-xl">
                      <FolderPlus className="size-4" />
                      첫 폴더 만들기
                    </Button>
                  }
                />
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

export default MyFoldersPage
