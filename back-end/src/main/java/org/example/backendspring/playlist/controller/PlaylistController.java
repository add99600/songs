package org.example.backendspring.playlist.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.backendspring.playlist.dto.*;
import org.example.backendspring.playlist.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 플레이리스트 CRUD 및 곡 관리 REST API 컨트롤러.
 */
@Validated
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    /**
     * 현재 로그인 사용자의 플레이리스트 목록을 조회한다.
     */
    @GetMapping
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<PlaylistResponse> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(playlists);
    }

    /**
     * 공개 플레이리스트 목록을 조회한다.
     */
    @GetMapping("/public")
    public ResponseEntity<List<PlaylistResponse>> getPublicPlaylists() {
        List<PlaylistResponse> playlists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(playlists);
    }

    /**
     * 새 플레이리스트를 생성한다.
     */
    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            Authentication authentication,
            @Valid @RequestBody CreatePlaylistRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        PlaylistResponse playlist = playlistService.createPlaylist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }

    /**
     * 플레이리스트 정보를 수정한다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponse> updatePlaylist(
            Authentication authentication,
            @Positive @PathVariable Long id,
            @Valid @RequestBody UpdatePlaylistRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        PlaylistResponse playlist = playlistService.updatePlaylist(userId, id, request);
        return ResponseEntity.ok(playlist);
    }

    /**
     * 플레이리스트를 삭제한다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(
            Authentication authentication,
            @Positive @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        playlistService.deletePlaylist(userId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 플레이리스트에 포함된 노래 목록을 조회한다.
     */
    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<List<PlaylistSongResponse>> getSongs(
            @Positive @PathVariable Long playlistId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<PlaylistSongResponse> songs = playlistService.getSongsByPlaylistId(playlistId, userId);
        return ResponseEntity.ok(songs);
    }

    /**
     * 플레이리스트에 노래를 추가한다.
     */
    @PostMapping("/{id}/songs")
    public ResponseEntity<PlaylistSongResponse> addSong(
            Authentication authentication,
            @Positive @PathVariable Long id,
            @Valid @RequestBody AddSongRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        PlaylistSongResponse song = playlistService.addSong(userId, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(song);
    }

    /**
     * 플레이리스트에서 노래를 제거한다.
     */
    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Void> removeSong(
            Authentication authentication,
            @Positive @PathVariable Long id,
            @PathVariable String songId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        playlistService.removeSong(userId, id, songId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 현재 사용자에게 공유된 플레이리스트 목록을 조회한다.
     */
    @GetMapping("/shared")
    public ResponseEntity<List<PlaylistResponse>> getSharedPlaylists(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<PlaylistResponse> playlists = playlistService.getSharedPlaylists(userId);
        return ResponseEntity.ok(playlists);
    }
}
