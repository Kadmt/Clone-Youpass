package com.youpass.backend.dto.response;


import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PassageDetailDto {
    private Long id;
    private String title;
    private String content;
    private List<QuestionDto> questions;
}
