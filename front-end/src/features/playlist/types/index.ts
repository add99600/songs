export interface Playlist {
  id: number
  name: string
  description: string | null
  isPublic: boolean
  createdAt: string
}

export interface PlaylistSong {
  id: number
  songId: string
  songNo: string | null
  songTitle: string | null
  songSinger: string | null
  songBrand: string | null
  sortOrder: number
  lyricsNote: string | null
  extraInfo: string | null
  addedAt: string
}
