import { useState } from 'react'
import { Search, Disc3 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { useSearchSongs } from '@/features/search/hooks/useSearchSongs'
import { SearchResultList } from '@/features/search/components/SearchResultList'

const SearchPage = () => {
  const [searchQuery, setSearchQuery] = useState('')
  const { results, hasSearched, loading, search } = useSearchSongs()

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    search(searchQuery)
  }

  return (
    <div className="flex flex-col">
      <div className="relative bg-gradient-to-b from-primary/8 via-primary/3 to-transparent pt-8 pb-12 px-4 sm:px-6">
        <div className="container max-w-2xl mx-auto text-center space-y-6">
          <div>
            <h1 className="text-3xl sm:text-4xl font-bold tracking-tight">
              어떤 노래를 찾으시나요?
            </h1>
            <p className="text-muted-foreground mt-2">
              TJ/KY 노래방 번호를 바로 확인하세요
            </p>
          </div>

          <form onSubmit={handleSearch} className="relative max-w-xl mx-auto">
            <div className="relative">
              <Search className="absolute left-4 top-1/2 -translate-y-1/2 size-5 text-muted-foreground" />
              <Input
                placeholder="곡 제목 또는 가수명으로 검색..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-12 pr-24 h-13 text-base rounded-xl border-2 border-border/50 focus:border-primary/50 shadow-sm bg-background"
                aria-label="노래 검색"
              />
              <Button
                type="submit"
                className="absolute right-1.5 top-1/2 -translate-y-1/2 h-10 px-5 rounded-lg"
                disabled={loading}
              >
                {loading ? <Disc3 className="size-4 animate-spin" /> : '검색'}
              </Button>
            </div>
          </form>
        </div>
      </div>

      <div className="container max-w-2xl mx-auto px-4 sm:px-6 -mt-2 pb-8">
        <SearchResultList results={results} hasSearched={hasSearched} loading={loading} />
      </div>
    </div>
  )
}

export default SearchPage
