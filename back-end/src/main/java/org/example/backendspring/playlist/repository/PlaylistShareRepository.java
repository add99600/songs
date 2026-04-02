package org.example.backendspring.playlist.repository;

import org.example.backendspring.playlist.entity.PlaylistShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 플레이리스트 공유 JPA 리포지토리.
 */
@Repository
public interface PlaylistShareRepository extends JpaRepository<PlaylistShare, Long> {

    /**
     * 플레이리스트에 대한 공유 목록을 조회한다.
     */
    List<PlaylistShare> findByPlaylistId(Long playlistId);

    /**
     * 특정 사용자에게 공유된 목록을 조회한다.
     */
    List<PlaylistShare> findBySharedWithUserId(Long sharedWithUserId);

    /**
     * 플레이리스트 ID와 공유 대상 사용자 ID로 공유 정보를 조회한다.
     */
    Optional<PlaylistShare> findByPlaylistIdAndSharedWithUserId(Long playlistId, Long sharedWithUserId);

    /**
     * 플레이리스트-사용자 간 공유 존재 여부를 확인한다.
     */
    boolean existsByPlaylistIdAndSharedWithUserId(Long playlistId, Long sharedWithUserId);

    /**
     * 플레이리스트-사용자 간 공유를 삭제한다.
     */
    void deleteByPlaylistIdAndSharedWithUserId(Long playlistId, Long sharedWithUserId);
}
