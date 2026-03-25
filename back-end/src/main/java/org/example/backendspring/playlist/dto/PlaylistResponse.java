package org.example.backendspring.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backendspring.playlist.entity.Playlist;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {

    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private OffsetDateTime createdAt;

    public static PlaylistResponse from(Playlist playlist) {
        return new PlaylistResponse(
            playlist.getId(),
            playlist.getName(),
            playlist.getDescription(),
            playlist.isPublic(),
            playlist.getCreatedAt()
        );
    }
}
