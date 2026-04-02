package org.example.backendspring.playlist.repository;

import org.example.backendspring.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 플레이리스트 JPA 리포지토리.
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    /**
     * 소유자 ID로 플레이리스트 목록을 조회한다.
     */
    List<Playlist> findByOwnerId(Long ownerId);

    /**
     * 공개 플레이리스트 목록을 조회한다.
     */
    List<Playlist> findByIsPublicTrue();

    /**
     * 플레이리스트 ID와 소유자 ID로 존재 여부를 확인한다.
     */
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
