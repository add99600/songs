package org.example.backendspring.playlist.service;

import org.example.backendspring.auth.entity.User;
import org.example.backendspring.auth.repository.UserRepository;
import org.example.backendspring.playlist.dto.*;
import org.example.backendspring.playlist.entity.Playlist;
import org.example.backendspring.playlist.entity.PlaylistShare;
import org.example.backendspring.playlist.entity.PlaylistSong;
import org.example.backendspring.playlist.repository.PlaylistRepository;
import org.example.backendspring.playlist.repository.PlaylistShareRepository;
import org.example.backendspring.playlist.repository.PlaylistSongRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistShareRepository playlistShareRepository;
    private final UserRepository userRepository;

    public PlaylistService(
            PlaylistRepository playlistRepository,
            PlaylistSongRepository playlistSongRepository,
            PlaylistShareRepository playlistShareRepository,
            UserRepository userRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.playlistShareRepository = playlistShareRepository;
        this.userRepository = userRepository;
    }

    public List<PlaylistResponse> getPlaylistsByUserId(Long userId) {
        List<Playlist> playlists = playlistRepository.findByOwnerId(userId);
        return playlists.stream().map(PlaylistResponse::from).collect(Collectors.toList());
    }

    public List<PlaylistResponse> getPublicPlaylists() {
        List<Playlist> playlists = playlistRepository.findByIsPublicTrue();
        return playlists.stream().map(PlaylistResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public PlaylistResponse createPlaylist(Long userId, CreatePlaylistRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + userId));

        Playlist playlist = new Playlist();
        playlist.setOwner(owner);
        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setPublic(request.isPublic());

        Playlist saved = playlistRepository.save(playlist);
        return PlaylistResponse.from(saved);
    }

    @Transactional
    public PlaylistResponse updatePlaylist(Long userId, Long playlistId, UpdatePlaylistRequest request) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        verifyOwner(playlist, userId);

        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setPublic(request.isPublic());

        Playlist saved = playlistRepository.save(playlist);
        return PlaylistResponse.from(saved);
    }

    @Transactional
    public void deletePlaylist(Long userId, Long playlistId) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        verifyOwner(playlist, userId);

        playlistSongRepository.deleteByPlaylistId(playlistId);
        playlistRepository.delete(playlist);
    }

    public List<PlaylistSongResponse> getSongsByPlaylistId(Long playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new NoSuchElementException("플레이리스트를 찾을 수 없습니다. id=" + playlistId);
        }

        List<PlaylistSong> songs = playlistSongRepository.findByPlaylistIdOrderBySortOrderAsc(playlistId);

        if (songs.isEmpty()) {
            return Collections.emptyList();
        }

        return songs.stream().map(PlaylistSongResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public PlaylistSongResponse addSong(Long userId, Long playlistId, AddSongRequest request) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        verifyOwner(playlist, userId);

        if (playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, request.getSongId())) {
            throw new IllegalArgumentException("이미 플레이리스트에 추가된 곡입니다. songId=" + request.getSongId());
        }

        List<PlaylistSong> existing = playlistSongRepository.findByPlaylistIdOrderBySortOrderAsc(playlistId);
        int nextOrder = existing.isEmpty() ? 0 : existing.get(existing.size() - 1).getSortOrder() + 1;

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSongId(request.getSongId());
        playlistSong.setSortOrder(nextOrder);

        PlaylistSong saved = playlistSongRepository.save(playlistSong);
        return PlaylistSongResponse.from(saved);
    }

    @Transactional
    public void removeSong(Long userId, Long playlistId, String songId) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        verifyOwner(playlist, userId);

        PlaylistSong playlistSong = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(() -> new NoSuchElementException("플레이리스트에서 곡을 찾을 수 없습니다. songId=" + songId));

        playlistSongRepository.delete(playlistSong);
    }

    public List<PlaylistResponse> getSharedPlaylists(Long userId) {
        List<PlaylistShare> shares = playlistShareRepository.findBySharedWithUserId(userId);
        return shares.stream().map(share -> PlaylistResponse.from(share.getPlaylist())).collect(Collectors.toList());
    }

    private Playlist findPlaylistOrThrow(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("플레이리스트를 찾을 수 없습니다. id=" + playlistId));
    }

    private static void verifyOwner(Playlist playlist, Long userId) {
        if (!playlist.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("해당 플레이리스트에 대한 권한이 없습니다.");
        }
    }
}
