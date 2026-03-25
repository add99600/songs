package org.example.backendspring.playlist.repository;

import org.example.backendspring.playlist.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    List<PlaylistSong> findByPlaylistIdOrderBySortOrderAsc(Long playlistId);

    boolean existsByPlaylistIdAndSongId(Long playlistId, String songId);

    Optional<PlaylistSong> findByPlaylistIdAndSongId(Long playlistId, String songId);

    void deleteByPlaylistId(Long playlistId);
}
