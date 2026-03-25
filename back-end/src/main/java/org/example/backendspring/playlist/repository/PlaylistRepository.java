package org.example.backendspring.playlist.repository;

import org.example.backendspring.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByOwnerId(Long ownerId);

    List<Playlist> findByIsPublicTrue();

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
