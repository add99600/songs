package org.example.backendspring.song.repository;

import org.example.backendspring.song.entity.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 노래 MongoDB 리포지토리.
 */
@Repository
public interface SongRepository extends MongoRepository<Song, String> {

    /**
     * 제목으로 노래를 검색한다 (대소문자 무시).
     */
    List<Song> findByTitleContainingIgnoreCase(String title);

    /**
     * 가수명으로 노래를 검색한다 (대소문자 무시).
     */
    List<Song> findBySingerContainingIgnoreCase(String singer);

    /**
     * 곡번호로 노래를 조회한다.
     */
    List<Song> findByNo(String no);
}
