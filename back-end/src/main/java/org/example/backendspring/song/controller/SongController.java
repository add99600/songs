package org.example.backendspring.song.controller;

import org.example.backendspring.song.dto.LyricsResponse;
import org.example.backendspring.song.dto.PagedSongResponse;
import org.example.backendspring.song.entity.Song;
import org.example.backendspring.song.service.LyricsService;
import org.example.backendspring.song.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 노래 검색 및 가사 조회 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;
    private final LyricsService lyricsService;

    public SongController(SongService songService, LyricsService lyricsService) {
        this.songService = songService;
        this.lyricsService = lyricsService;
    }

    /**
     * 통합 검색 (MongoDB 우선 → 외부 API fallback). 페이징 + 캐시 적용.
     */
    @GetMapping
    public ResponseEntity<PagedSongResponse> search(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedSongResponse response = songService.searchPaged(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 외부 API로 제목 검색.
     */
    @GetMapping("/external/title")
    public ResponseEntity<List<Song>> searchExternalByTitle(
            @RequestParam String keyword
    ) {
        List<Song> results = songService.searchFromExternalByTitle(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * 외부 API로 가수 검색.
     */
    @GetMapping("/external/singer")
    public ResponseEntity<List<Song>> searchExternalBySinger(
            @RequestParam String keyword
    ) {
        List<Song> results = songService.searchFromExternalBySinger(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * 외부 API 최신곡 조회.
     */
    @GetMapping("/external/latest")
    public ResponseEntity<List<Song>> getLatestFromExternal() {
        List<Song> results = songService.getLatestFromExternal();
        return ResponseEntity.ok(results);
    }

    /**
     * ID로 노래를 단건 조회한다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Song> findById(@PathVariable String id) {
        Song song = songService.findById(id);
        if (song == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(song);
    }

    /**
     * 곡번호로 가사 조회 (Caffeine 캐시 적용).
     */
    @GetMapping("/{songNo}/lyrics")
    public ResponseEntity<LyricsResponse> getLyrics(
            @PathVariable String songNo,
            @RequestParam(defaultValue = "tj") String brand,
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "") String singer
    ) {
        LyricsResponse response = lyricsService.getLyrics(songNo, brand, title, singer);
        return ResponseEntity.ok(response);
    }
}
