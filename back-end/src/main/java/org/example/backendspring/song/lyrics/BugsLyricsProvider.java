package org.example.backendspring.song.lyrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bugs Music 기반 가사 조회 제공자.
 */
@Component
public class BugsLyricsProvider implements LyricsProvider {

    private static final Logger log = LoggerFactory.getLogger(BugsLyricsProvider.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private static final Pattern TRACK_ID_PATTERN = Pattern.compile("/track/(\\d+)");
    private static final Pattern XMP_PATTERN = Pattern.compile("<xmp>([\\s\\S]*?)</xmp>", Pattern.CASE_INSENSITIVE);
    private static final Pattern LYRICS_CONTAINER_PATTERN = Pattern.compile(
            "lyricsContainer[^>]*>([\\s\\S]*?)</div>", Pattern.CASE_INSENSITIVE);

    private final RestClient restClient;

    public BugsLyricsProvider(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://music.bugs.co.kr")
                .defaultHeader("User-Agent", USER_AGENT)
                .build();
    }

    @Override
    public String fetchLyrics(String songNo, String brand, String title, String singer) {
        if (title == null || title.isBlank() || singer == null || singer.isBlank()) {
            return null;
        }

        try {
            // 1. 검색
            String query = singer + " " + title;
            String searchHtml = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/track")
                            .queryParam("q", query)
                            .build())
                    .retrieve()
                    .body(String.class);

            if (searchHtml == null || searchHtml.isEmpty()) {
                return null;
            }

            // 2. 트랙 ID 추출
            Matcher trackMatcher = TRACK_ID_PATTERN.matcher(searchHtml);
            if (!trackMatcher.find()) {
                log.debug("Bugs 검색 결과에서 트랙 ID를 찾을 수 없음: query={}", query);
                return null;
            }
            String trackId = trackMatcher.group(1);

            // 3. 트랙 페이지 조회
            String trackHtml = restClient.get()
                    .uri("/track/" + trackId)
                    .retrieve()
                    .body(String.class);

            if (trackHtml == null || trackHtml.isEmpty()) {
                return null;
            }

            // 4. 가사 추출 (xmp 태그 우선)
            Matcher xmpMatcher = XMP_PATTERN.matcher(trackHtml);
            if (xmpMatcher.find()) {
                String rawLyrics = xmpMatcher.group(1).trim();
                log.info("Bugs Music 가사 조회 성공: title={}, singer={}", title, singer);
                return cleanHtmlTags(rawLyrics);
            }

            // lyricsContainer fallback
            Matcher containerMatcher = LYRICS_CONTAINER_PATTERN.matcher(trackHtml);
            if (containerMatcher.find()) {
                String rawLyrics = containerMatcher.group(1).trim();
                log.info("Bugs Music 가사 조회 성공: title={}, singer={}", title, singer);
                return cleanHtmlTags(rawLyrics);
            }

            log.debug("Bugs 트랙 페이지에서 가사를 찾을 수 없음: trackId={}", trackId);
            return null;
        } catch (Exception e) {
            log.warn("Bugs Music 가사 조회 실패: title={}, singer={}, error={}", title, singer, e.getMessage());
            return null;
        }
    }

    private static String cleanHtmlTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]+>", "")
                   .replaceAll("&amp;", "&")
                   .replaceAll("&lt;", "<")
                   .replaceAll("&gt;", ">")
                   .replaceAll("&quot;", "\"")
                   .replaceAll("&#39;", "'")
                   .replaceAll("&nbsp;", " ")
                   .trim();
    }
}
