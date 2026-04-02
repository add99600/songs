package org.example.backendspring.song.service;

import org.example.backendspring.song.dto.LyricsResponse;
import org.example.backendspring.song.lyrics.LyricsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 가사 조회 비즈니스 로직 서비스.
 */
@Service
public class LyricsService {

    private static final Logger log = LoggerFactory.getLogger(LyricsService.class);

    private final LyricsProvider lyricsProvider;

    public LyricsService(LyricsProvider lyricsProvider) {
        this.lyricsProvider = lyricsProvider;
    }

    /**
     * 곡번호, 브랜드, 제목, 가수명으로 가사를 조회한다. 결과는 Caffeine 캐시에 저장된다.
     */
    @Cacheable(value = "lyrics", key = "#songNo + ':' + #brand", unless = "#result.lyrics == ''")
    public LyricsResponse getLyrics(String songNo, String brand, String title, String singer) {
        log.info("가사 조회 (캐시 미스): songNo={}, brand={}, title={}, singer={}", songNo, brand, title, singer);

        String lyrics = lyricsProvider.fetchLyrics(songNo, brand, title, singer);

        if (lyrics == null || lyrics.isBlank()) {
            return LyricsResponse.empty(songNo, brand);
        }

        return LyricsResponse.of(songNo, brand, lyrics, "external");
    }
}
