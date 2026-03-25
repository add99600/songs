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
public class UpdatePlaylistRequest {

    @NotBlank(message = "플레이리스트 이름은 필수입니다.")
    @Size(max = 100, message = "플레이리스트 이름은 100자 이내여야 합니다.")
    private String name;

    private String description;

    private boolean isPublic = false;
}
