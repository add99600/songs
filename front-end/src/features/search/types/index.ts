export interface Song {
  id: string
  no: string
  title: string
  singer: string
  brand: string
  releaseDate?: string
}

export interface PagedSongResponse {
  content: Song[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}
