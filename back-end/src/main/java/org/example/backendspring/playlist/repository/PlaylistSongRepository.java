package org.example.backendspring.playlist.repository;

import org.example.backendspring.playlist.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 플레이리스트-노래 매핑 JPA 리포지토리.
 */
@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    /**
     * 플레이리스트의 노래 목록을 정렬 순서대로 조회한다.
     */
    List<PlaylistSong> findByPlaylistIdOrderBySortOrderAsc(Long playlistId);

    /**
     * 플레이리스트에 특정 곡이 존재하는지 확인한다.
     */
    boolean existsByPlaylistIdAndSongId(Long playlistId, String songId);

    /**
     * 플레이리스트 ID와 곡 ID로 매핑을 조회한다.
     */
    Optional<PlaylistSong> findByPlaylistIdAndSongId(Long playlistId, String songId);

    /**
     * 플레이리스트에 포함된 모든 곡 매핑을 삭제한다.
     */
    void deleteByPlaylistId(Long playlistId);
}
