package com.youpass.backend.common.utils;

import com.youpass.backend.dto.response.QuestionResultDto;
import com.youpass.backend.dto.response.QuestionReviewDetailDto;
import com.youpass.backend.entity.GradableQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScoringService {

    @Autowired
    private ObjectMapper objectMapper;

    public record GradeResult(int correctCount, List<QuestionResultDto> results) {}

    // 1. Chấm điểm khi nộp bài: tính số câu đúng & sinh danh sách QuestionResultDto (gọn nhẹ)
    public <T extends GradableQuestion> GradeResult gradeSubmission(List<T> questions, Map<Long, String> userAnswers) {
        int correctCount = 0;
        List<QuestionResultDto> results = new ArrayList<>();
        Map<Long, String> safeAnswers = userAnswers != null ? userAnswers : Collections.emptyMap();
        List<T> safeQuestions = questions != null ? questions : Collections.emptyList();

        for (T q : safeQuestions) {
            String userAnswer = safeAnswers.get(q.getId());
            boolean isCorrect = userAnswer != null
                    && q.getCorrectAnswer() != null
                    && userAnswer.trim().equalsIgnoreCase(q.getCorrectAnswer().trim());

            if (isCorrect) {
                correctCount++;
            }

            results.add(QuestionResultDto.builder()
                    .questionId(q.getId())
                    .userAnswer(userAnswer)
                    .correctAnswer(q.getCorrectAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        return new GradeResult(correctCount, results);
    }

    // 2. Dựng chi tiết từng câu hỏi cho trang Review (kèm đề, options, đáp án chi tiết)
    public <T extends GradableQuestion> List<QuestionReviewDetailDto> buildReviewDetails(
            List<T> questions, Map<Long, String> userAnswers) {
        List<QuestionReviewDetailDto> results = new ArrayList<>();
        Map<Long, String> safeAnswers = userAnswers != null ? userAnswers : Collections.emptyMap();
        List<T> safeQuestions = questions != null ? questions : Collections.emptyList();

        for (T q : safeQuestions) {
            String userAnswer = safeAnswers.get(q.getId());
            boolean isCorrect = userAnswer != null
                    && q.getCorrectAnswer() != null
                    && userAnswer.trim().equalsIgnoreCase(q.getCorrectAnswer().trim());

            results.add(QuestionReviewDetailDto.builder()
                    .questionId(q.getId())
                    .questionText(q.getQuestionText())
                    .questionType(q.getQuestionType())
                    .optionsJson(q.getOptionsJson())
                    .userAnswer(userAnswer)
                    .correctAnswer(q.getCorrectAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        return results;
    }

    // 3. Helper parse chuỗi answerData từ database (dạng JSON hoặc fallback format {1=A, 2=B})
    public Map<Long, String> parseAnswerData(String answerData) {
        Map<Long, String> result = new HashMap<>();
        if (answerData == null || answerData.isBlank()) {
            return result;
        }

        // 1. Parse JSON chuẩn
        try {
            return objectMapper.readValue(answerData, new TypeReference<Map<Long, String>>() {});
        } catch (Exception ignored) {
        }

        // 2. Fallback nếu dữ liệu lưu dạng Map.toString(): {1=A, 2=B}
        String clean = answerData.trim();
        if (clean.startsWith("{") && clean.endsWith("}")) {
            clean = clean.substring(1, clean.length() - 1);
            if (!clean.isBlank()) {
                String[] pairs = clean.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        try {
                            result.put(Long.parseLong(keyValue[0].trim()), keyValue[1].trim());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return result;
    }
}
