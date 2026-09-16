package com.youpass.backend.dto.request;


import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class SubmitAnswersRequest {
    private Map<Long, String> answers;  //key: questionId, value: answer
}