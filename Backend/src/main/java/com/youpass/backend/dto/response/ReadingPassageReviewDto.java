package com.youpass.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReadingPassageReviewDto {
    private Long submissionId;
    private Long passageId;
    private String title;
    private String content;
    private Integer score;
    private Integer totalQuestions;
    private Integer duration;
    private LocalDateTime createdAt;
    private List<QuestionReviewDetailDto> questions;
}
