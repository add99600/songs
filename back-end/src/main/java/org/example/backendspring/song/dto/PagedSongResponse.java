package org.example.backendspring.song.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backendspring.song.entity.Song;

import java.util.List;

@Getter
@AllArgsConstructor
public class PagedSongResponse {
    private List<Song> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static PagedSongResponse of(List<Song> allResults, int page, int size) {
        int total = allResults.size();
        int totalPages = (int) Math.ceil((double) total / size);
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<Song> content = allResults.subList(fromIndex, toIndex);
        return new PagedSongResponse(content, page, size, total, totalPages);
    }
}
