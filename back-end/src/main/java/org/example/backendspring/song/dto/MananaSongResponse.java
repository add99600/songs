package org.example.backendspring.song.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MananaSongResponse {

    private String brand;
    private String no;
    private String title;
    private String singer;
    private String composer;
    private String lyricist;
    private String release;
}
