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

/**
 * 플레이리스트 비즈니스 로직 서비스.
 */
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

    /**
     * 사용자의 플레이리스트 목록을 조회한다.
     */
    public List<PlaylistResponse> getPlaylistsByUserId(Long userId) {
        List<Playlist> playlists = playlistRepository.findByOwnerId(userId);
        return playlists.stream().map(PlaylistResponse::from).collect(Collectors.toList());
    }

    /**
     * 공개 플레이리스트 목록을 조회한다.
     */
    public List<PlaylistResponse> getPublicPlaylists() {
        List<Playlist> playlists = playlistRepository.findByIsPublicTrue();
        return playlists.stream().map(PlaylistResponse::from).collect(Collectors.toList());
    }

    /**
     * 새 플레이리스트를 생성한다.
     */
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

    /**
     * 플레이리스트 정보를 수정한다.
     */
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

    /**
     * 플레이리스트와 포함된 곡을 모두 삭제한다.
     */
    @Transactional
    public void deletePlaylist(Long userId, Long playlistId) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        verifyOwner(playlist, userId);

        playlistSongRepository.deleteByPlaylistId(playlistId);
        playlistRepository.delete(playlist);
    }

    /**
     * 플레이리스트에 포함된 노래 목록을 정렬 순서대로 조회한다.
     */
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

    /**
     * 플레이리스트에 노래를 추가한다. 중복 곡은 허용하지 않는다.
     */
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
        playlistSong.setSongNo(request.getSongNo());
        playlistSong.setSongTitle(request.getTitle());
        playlistSong.setSongSinger(request.getSinger());
        playlistSong.setSongBrand(request.getBrand());
        playlistSong.setSortOrder(nextOrder);

        PlaylistSong saved = playlistSongRepository.save(playlistSong);
        return PlaylistSongResponse.from(saved);
    }

    /**
     * 플레이리스트에서 노래를 제거한다.
     */
    @Transactional
    public void removeSong(Long userId, Long playlistId, String songId) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        verifyOwner(playlist, userId);

        PlaylistSong playlistSong = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(() -> new NoSuchElementException("플레이리스트에서 곡을 찾을 수 없습니다. songId=" + songId));

        playlistSongRepository.delete(playlistSong);
    }

    /**
     * 사용자에게 공유된 플레이리스트 목록을 조회한다.
     */
    public List<PlaylistResponse> getSharedPlaylists(Long userId) {
        List<PlaylistShare> shares = playlistShareRepository.findBySharedWithUserId(userId);
        return shares.stream().map(share -> PlaylistResponse.from(share.getPlaylist())).collect(Collectors.toList());
    }

    // 플레이리스트 조회, 없으면 예외 발생
    private Playlist findPlaylistOrThrow(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("플레이리스트를 찾을 수 없습니다. id=" + playlistId));
    }

    // 플레이리스트 소유자 검증
    private static void verifyOwner(Playlist playlist, Long userId) {
        if (!playlist.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("해당 플레이리스트에 대한 권한이 없습니다.");
        }
    }
}
