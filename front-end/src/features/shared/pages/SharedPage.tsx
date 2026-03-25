import { Users, RefreshCw } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { useSharedPlaylists } from '@/features/shared/hooks/useSharedPlaylists'
import { SharedPlaylistCard } from '@/features/shared/components/SharedPlaylistCard'

const SharedPage = () => {
  const { sharedList, loading, error, refetch } = useSharedPlaylists()

  return (
    <div className="flex flex-col">
      <div className="bg-gradient-to-b from-accent/8 to-transparent pt-8 pb-6 px-4 sm:px-6">
        <div className="container max-w-3xl mx-auto">
          <h1 className="text-3xl font-bold tracking-tight">함께 듣는 믹스</h1>
          <p className="text-muted-foreground mt-1">다른 사람들이 공유한 플레이리스트를 둘러보세요</p>
        </div>
      </div>

      <div className="container max-w-3xl mx-auto px-4 sm:px-6 pb-8">
        {loading && (
          <div className="space-y-3 pt-2">
            {Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="rounded-xl border bg-card p-5 flex items-center gap-4">
                <Skeleton className="size-11 rounded-full" />
                <div className="flex-1 space-y-2">
                  <Skeleton className="h-5 w-44" />
                  <Skeleton className="h-3 w-28" />
                </div>
                <Skeleton className="h-7 w-16 rounded-full" />
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
            {sharedList.length > 0 ? (
              <div className="space-y-3 pt-2">
                {sharedList.map((item, idx) => (
                  <SharedPlaylistCard key={item.id} item={item} index={idx} />
                ))}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-20 text-center">
                <div className="w-24 h-24 rounded-3xl bg-accent/5 flex items-center justify-center mb-6">
                  <Users className="size-12 text-accent/25" />
                </div>
                <h3 className="font-semibold text-lg">공유된 폴더가 없어요</h3>
                <p className="text-sm text-muted-foreground mt-1 max-w-xs">
                  다른 사용자들이 플레이리스트를 공유하면<br />여기에 표시됩니다
                </p>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

export default SharedPage
