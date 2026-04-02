package org.example.backendspring.song.lyrics;

/**
 * 가사 조회 제공자 인터페이스.
 */
public interface LyricsProvider {

    /**
     * 곡번호, 브랜드, 제목, 가수명으로 가사를 조회한다.
     *
     * @param songNo 곡번호 (예: "12345")
     * @param brand  노래방 브랜드 (예: "tj", "ky")
     * @param title  노래 제목
     * @param singer 가수명
     * @return 가사 문자열, 가사를 찾지 못하면 null
     */
    String fetchLyrics(String songNo, String brand, String title, String singer);
}
