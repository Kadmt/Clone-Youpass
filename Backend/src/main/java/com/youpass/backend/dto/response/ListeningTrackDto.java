package com.youpass.backend.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ListeningTrackDto {
    private Long id;
    private String title;
    private Long orderIndex;
    private String audioUrl;
    private List<QuestionDto> questions;
}
