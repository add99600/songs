package org.example.backendspring.song.client;

import org.example.backendspring.song.dto.MananaSongResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * api.manana.kr 노래방 API 클라이언트.
 *
 * 크롤링 데이터(MongoDB)가 없거나 실패할 경우 fallback으로 사용.
 * - 곡 제목 검색: /karaoke/song/{title}/{brand}.json
 * - 가수 검색:   /karaoke/singer/{singer}/{brand}.json
 * - 최신곡:      /karaoke/{brand}.json
 */
@Component
public class MananaKaraokeClient {

    private static final Logger log = LoggerFactory.getLogger(MananaKaraokeClient.class);

    private final RestClient restClient;
    private final String defaultBrand;

    public MananaKaraokeClient(
            RestClient.Builder restClientBuilder,
            @Value("${manana.api.base-url:https://api.manana.kr}") String baseUrl,
            @Value("${manana.api.default-brand:tj}") String defaultBrand
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = restClientBuilder.requestFactory(factory).baseUrl(baseUrl).build();
        this.defaultBrand = defaultBrand;
    }

    public List<MananaSongResponse> searchByTitle(String title) {
        return searchByTitle(title, defaultBrand);
    }

    public List<MananaSongResponse> searchByTitle(String title, String brand) {
        String encoded = UriUtils.encodePath(title, StandardCharsets.UTF_8);
        String uri = "/karaoke/song/" + encoded + "/" + brand + ".json";
        return fetch(uri);
    }

    public List<MananaSongResponse> searchBySinger(String singer) {
        return searchBySinger(singer, defaultBrand);
    }

    public List<MananaSongResponse> searchBySinger(String singer, String brand) {
        String encoded = UriUtils.encodePath(singer, StandardCharsets.UTF_8);
        String uri = "/karaoke/singer/" + encoded + "/" + brand + ".json";
        return fetch(uri);
    }

    public List<MananaSongResponse> getLatestSongs() {
        return getLatestSongs(defaultBrand);
    }

    public List<MananaSongResponse> getLatestSongs(String brand) {
        String uri = "/karaoke/" + brand + ".json";
        return fetch(uri);
    }

    private List<MananaSongResponse> fetch(String uri) {
        try {
            List<MananaSongResponse> result = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return result != null ? result : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Manana API 호출 실패: uri={}, error={}", uri, e.getMessage());
            return Collections.emptyList();
        }
    }
}
