package org.example.backendspring.song.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "tj_songs")
public class Song {

    @Id
    private String id;

    private String no;

    private String title;

    private String singer;

    private String brand;

    private String nationType;

    private String releaseDate;
}
