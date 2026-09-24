package com.youpass.backend.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswersRequest {
    private Map<Long, String> answers;  //key: questionId, value: answer
    private Integer duration;
}