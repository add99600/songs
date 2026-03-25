package org.example.backendspring.playlist.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backendspring.auth.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "playlist_shares",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_playlist_shares", columnNames = {"playlist_id", "shared_with_user_id"})
    }
)
public class PlaylistShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "playlist_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_playlist_shares_playlist_id")
    )
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "shared_with_user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_playlist_shares_shared_with_user_id")
    )
    private User sharedWithUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 10)
    private SharePermission permission = SharePermission.READ;

    @CreationTimestamp
    @Column(name = "shared_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime sharedAt;
}
