package com.youpass.backend.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SubmissionResultDto {
    private Long submissionId;
    private Long score;
    private Integer totalQuestions;
    private List<QuestionResultDto> results;


}