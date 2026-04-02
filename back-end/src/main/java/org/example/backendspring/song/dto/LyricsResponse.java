package org.example.backendspring.song.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LyricsResponse {

    private String songNo;
    private String brand;
    private String lyrics;
    private String source;

    public static LyricsResponse empty(String songNo, String brand) {
        return new LyricsResponse(songNo, brand, "", "none");
    }

    public static LyricsResponse of(String songNo, String brand, String lyrics, String source) {
        return new LyricsResponse(songNo, brand, lyrics, source);
    }
}
