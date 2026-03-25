package org.example.backendspring.song.service;

import org.example.backendspring.song.client.MananaKaraokeClient;
import org.example.backendspring.song.dto.MananaSongResponse;
import org.example.backendspring.song.entity.Song;
import org.example.backendspring.song.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    private static final Logger log = LoggerFactory.getLogger(SongService.class);

    private final SongRepository songRepository;
    private final MongoTemplate mongoTemplate;
    private final MananaKaraokeClient mananaClient;

    public SongService(
            SongRepository songRepository,
            MongoTemplate mongoTemplate,
            MananaKaraokeClient mananaClient
    ) {
        this.songRepository = songRepository;
        this.mongoTemplate = mongoTemplate;
        this.mananaClient = mananaClient;
    }

    /**
     * 노래 검색 (MongoDB 우선, 결과 없으면 외부 API fallback).
     */
    public List<Song> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        String trimmed = keyword.trim();

        // 1차: MongoDB 검색
        List<Song> mongoResults = searchFromMongo(trimmed);
        if (!mongoResults.isEmpty()) {
            return mongoResults;
        }

        // 2차: 외부 API fallback
        log.info("MongoDB 결과 없음, 외부 API로 fallback 검색: keyword={}", trimmed);
        return searchFromExternalApi(trimmed);
    }

    /**
     * 외부 API로만 검색 (제목 기준).
     */
    public List<Song> searchFromExternalByTitle(String title) {
        if (title == null || title.isBlank()) {
            return Collections.emptyList();
        }
        List<MananaSongResponse> results = mananaClient.searchByTitle(title.trim());
        return results.stream().map(this::toSong).collect(Collectors.toList());
    }

    /**
     * 외부 API로만 검색 (가수 기준).
     */
    public List<Song> searchFromExternalBySinger(String singer) {
        if (singer == null || singer.isBlank()) {
            return Collections.emptyList();
        }
        List<MananaSongResponse> results = mananaClient.searchBySinger(singer.trim());
        return results.stream().map(this::toSong).collect(Collectors.toList());
    }

    /**
     * 외부 API에서 최신곡 조회.
     */
    public List<Song> getLatestFromExternal() {
        List<MananaSongResponse> results = mananaClient.getLatestSongs();
        return results.stream().map(this::toSong).collect(Collectors.toList());
    }

    public Song findById(String id) {
        return songRepository.findById(id).orElse(null);
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    private List<Song> searchFromMongo(String keyword) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("title").regex(keyword, "i"),
                Criteria.where("singer").regex(keyword, "i")
        );
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Song.class);
    }

    private List<Song> searchFromExternalApi(String keyword) {
        // 제목 검색 먼저 시도
        List<MananaSongResponse> byTitle = mananaClient.searchByTitle(keyword);
        if (!byTitle.isEmpty()) {
            return byTitle.stream().map(this::toSong).collect(Collectors.toList());
        }

        // 제목 결과 없으면 가수명으로 검색
        List<MananaSongResponse> bySinger = mananaClient.searchBySinger(keyword);
        return bySinger.stream().map(this::toSong).collect(Collectors.toList());
    }

    private Song toSong(MananaSongResponse res) {
        Song song = new Song();
        song.setNo(res.getNo());
        song.setTitle(res.getTitle());
        song.setSinger(res.getSinger());
        song.setBrand(res.getBrand());
        song.setReleaseDate(res.getRelease());
        return song;
    }
}
