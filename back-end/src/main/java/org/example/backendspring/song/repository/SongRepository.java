package org.example.backendspring.song.repository;

import org.example.backendspring.song.entity.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends MongoRepository<Song, String> {

    List<Song> findByTitleContainingIgnoreCase(String title);

    List<Song> findBySingerContainingIgnoreCase(String singer);

    List<Song> findByNo(String no);
}
