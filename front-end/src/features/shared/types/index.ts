export interface SharedPlaylist {
  id: number
  playlistId: number
  playlistName: string
  ownerNickname: string
  permission: 'READ' | 'EDIT'
  sharedAt: string
}
