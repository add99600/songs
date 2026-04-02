package org.example.backendspring.playlist.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "playlist_songs",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_playlist_songs", columnNames = {"playlist_id", "song_id"})
    }
)
public class PlaylistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "playlist_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_playlist_songs_playlist_id")
    )
    private Playlist playlist;

    @Column(name = "song_id", nullable = false, length = 200)
    private String songId;

    @Column(name = "song_no", length = 20)
    private String songNo;

    @Column(name = "song_title", length = 200)
    private String songTitle;

    @Column(name = "song_singer", length = 200)
    private String songSinger;

    @Column(name = "song_brand", length = 10)
    private String songBrand;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(name = "lyrics_note", columnDefinition = "TEXT")
    private String lyricsNote;

    @Column(name = "extra_info", columnDefinition = "TEXT")
    private String extraInfo;

    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime addedAt;
}
