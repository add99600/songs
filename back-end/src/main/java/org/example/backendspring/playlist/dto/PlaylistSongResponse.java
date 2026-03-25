package org.example.backendspring.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backendspring.playlist.entity.PlaylistSong;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongResponse {

    private Long id;
    private String songId;
    private int sortOrder;
    private String lyricsNote;
    private String extraInfo;
    private OffsetDateTime addedAt;

    public static PlaylistSongResponse from(PlaylistSong playlistSong) {
        return new PlaylistSongResponse(
            playlistSong.getId(),
            playlistSong.getSongId(),
            playlistSong.getSortOrder(),
            playlistSong.getLyricsNote(),
            playlistSong.getExtraInfo(),
            playlistSong.getAddedAt()
        );
    }
}
