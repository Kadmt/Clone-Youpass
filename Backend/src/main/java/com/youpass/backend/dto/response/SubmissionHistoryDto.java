package com.youpass.backend.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionHistoryDto {
    private Long id;
    private LocalDateTime createdAt;
    private Integer score;
    private String skillType;
    private String title;
}
