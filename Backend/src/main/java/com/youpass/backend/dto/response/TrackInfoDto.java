package com.youpass.backend.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrackInfoDto {
    private Long trackId;
    private String title;
    private String audioUrl;
    private String transcript;
}
