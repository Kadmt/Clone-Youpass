package com.youpass.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;


@Getter
@Builder
public class SubmissionListeningResultDto {
        private Long submissionId;
        private Long score;
        private TrackInfoDto trackInfo;
        private Integer totalQuestions;
        private Integer duration;
        private List<QuestionResultDto> results;

}

