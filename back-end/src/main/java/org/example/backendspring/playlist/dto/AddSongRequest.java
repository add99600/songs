package org.example.backendspring.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddSongRequest {

    @NotBlank(message = "곡 ID는 필수입니다.")
    private String songId;

    private String songNo;

    @NotBlank(message = "곡 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "가수명은 필수입니다.")
    private String singer;

    private String brand;
}
