package org.example.backendspring.song.controller;

import org.example.backendspring.song.entity.Song;
import org.example.backendspring.song.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    /**
     * 통합 검색 (MongoDB 우선 → 외부 API fallback).
     */
    @GetMapping
    public ResponseEntity<List<Song>> search(
            @RequestParam(required = false, defaultValue = "") String keyword
    ) {
        List<Song> results = songService.search(keyword);
        return ResponseEntity.ok(results);
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

    @GetMapping("/{id}")
    public ResponseEntity<Song> findById(@PathVariable String id) {
        Song song = songService.findById(id);
        if (song == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(song);
    }
}
