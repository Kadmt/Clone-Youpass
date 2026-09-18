package com.youpass.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionReviewDetailDto {
    private Long questionId;
    private String questionText;
    private String questionType;
    private String optionsJson;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}
