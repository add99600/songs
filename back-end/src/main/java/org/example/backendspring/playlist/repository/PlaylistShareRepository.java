package org.example.backendspring.playlist.repository;

import org.example.backendspring.playlist.entity.PlaylistShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistShareRepository extends JpaRepository<PlaylistShare, Long> {

    List<PlaylistShare> findByPlaylistId(Long playlistId);

    List<PlaylistShare> findBySharedWithUserId(Long sharedWithUserId);

    Optional<PlaylistShare> findByPlaylistIdAndSharedWithUserId(Long playlistId, Long sharedWithUserId);

    boolean existsByPlaylistIdAndSharedWithUserId(Long playlistId, Long sharedWithUserId);

    void deleteByPlaylistIdAndSharedWithUserId(Long playlistId, Long sharedWithUserId);
}
