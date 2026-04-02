import apiClient from '@/api/client'
import type { Playlist, PlaylistSong } from '@/features/playlist/types'

export const fetchPlaylists = async (): Promise<Playlist[]> => {
  const { data } = await apiClient.get<Playlist[]>('/api/playlists')
  return data
}

export interface CreatePlaylistParams {
  name: string
  description?: string
  isPublic: boolean
}

export const createPlaylist = async (params: CreatePlaylistParams): Promise<Playlist> => {
  const { data } = await apiClient.post<Playlist>('/api/playlists', params)
  return data
}

export const fetchPlaylistSongs = async (playlistId: number): Promise<PlaylistSong[]> => {
  const { data } = await apiClient.get<PlaylistSong[]>(`/api/playlists/${playlistId}/songs`)
  return data
}

export interface AddSongToPlaylistParams {
  songId: string
  songNo: string
  title: string
  singer: string
  brand: string
}

export const addSongToPlaylist = async (
  playlistId: number,
  params: AddSongToPlaylistParams
): Promise<PlaylistSong> => {
  const { data } = await apiClient.post<PlaylistSong>(
    `/api/playlists/${playlistId}/songs`,
    params
  )
  return data
}
