package com.youpass.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionDto {
    private Long id;
    private String questionType;
    private String questionText;
    private String optionsJson;

    // without correct answer
}
