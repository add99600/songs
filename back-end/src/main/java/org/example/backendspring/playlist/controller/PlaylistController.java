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

@Validated
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<PlaylistResponse> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/public")
    public ResponseEntity<List<PlaylistResponse>> getPublicPlaylists() {
        List<PlaylistResponse> playlists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(playlists);
    }

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            Authentication authentication,
            @Valid @RequestBody CreatePlaylistRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        PlaylistResponse playlist = playlistService.createPlaylist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(
            Authentication authentication,
            @Positive @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        playlistService.deletePlaylist(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<List<PlaylistSongResponse>> getSongs(
            @Positive @PathVariable Long playlistId
    ) {
        List<PlaylistSongResponse> songs = playlistService.getSongsByPlaylistId(playlistId);
        return ResponseEntity.ok(songs);
    }

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

    @GetMapping("/shared")
    public ResponseEntity<List<PlaylistResponse>> getSharedPlaylists(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<PlaylistResponse> playlists = playlistService.getSharedPlaylists(userId);
        return ResponseEntity.ok(playlists);
    }
}
