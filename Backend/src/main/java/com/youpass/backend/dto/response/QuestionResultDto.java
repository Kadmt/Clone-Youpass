package com.youpass.backend.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionResultDto {
    private Long questionId;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}