package org.example.backendspring.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 24, max = 24, message = "곡 ID는 24자리 MongoDB ObjectId여야 합니다.")
    private String songId;
}
