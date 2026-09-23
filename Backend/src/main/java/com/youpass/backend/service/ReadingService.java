package com.youpass.backend.service;

import com.youpass.backend.entity.*;
import com.youpass.backend.repository.TestGroupRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.youpass.backend.dto.request.SubmitAnswersRequest;
import com.youpass.backend.dto.response.*;
import com.youpass.backend.exception.business.ResourceNotFoundException;
import com.youpass.backend.repository.SubmissionRepository;
import com.youpass.backend.repository.UserRepository;
import com.youpass.backend.repository.reading.ReadingPassageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ReadingService {
    @Autowired
    private TestGroupRepository testGroupRepository;

    @Autowired
    private ReadingPassageRepository passageRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // lấy danh sách đề
    public List<PassageListDto> getAllPassages() {
        List<ReadingPassage> passages = passageRepository.findAll();

        return passages.stream()
                .map(passage -> PassageListDto.builder()
                        .id(passage.getId())
                        .title(passage.getTitle())
                        .build())
                .toList();
    }

    // chi tiết đề kèm câu hỏi (không chứa đáp án)
    public PassageDetailDto getPassageDetail(Long id) {
        ReadingPassage passage = passageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passage not found with id: " + id));

        List<QuestionDto> questionDtos = passage.getQuestions().stream().map(q -> QuestionDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .optionsJson(q.getOptionsJson())
                .build()).toList();

        return PassageDetailDto.builder()
                .id(passage.getId())
                .title(passage.getTitle())
                .content(passage.getContent())
                .questions(questionDtos)
                .build();
    }

    // nộp bài chấm điểm
    @Transactional
    public SubmissionReadingResultDto submitAnswers(Long userId, Long passageId, SubmitAnswersRequest request) {
        ReadingPassage passage = passageRepository.findById(passageId)
                .orElseThrow(() -> new ResourceNotFoundException("Passage not found with id: " + passageId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<ReadingQuestion> questions = passage.getQuestions();
        Map<Long, String> userAnswers = request.getAnswers() != null ? request.getAnswers() : new HashMap<>();

        int correctCount = 0;
        List<QuestionResultDto> results = new ArrayList<>();

        for (ReadingQuestion question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            boolean isCorrect = userAnswer != null && question.getCorrectAnswer().equalsIgnoreCase(userAnswer);
            if (isCorrect) {
                correctCount++;
            }

            results.add(QuestionResultDto.builder()
                    .questionId(question.getId())
                    .userAnswer(userAnswer)
                    .correctAnswer(question.getCorrectAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setSkillType("reading");
        submission.setReferenceId(passageId);
        submission.setScore(correctCount);

        try {
            submission.setAnswerData(objectMapper.writeValueAsString(userAnswers));
        } catch (Exception e) {
            submission.setAnswerData(userAnswers.toString());
        }

        submissionRepository.save(submission);

        return SubmissionReadingResultDto.builder()
                .submissionId(submission.getId())
                .score((long) correctCount)
                .totalQuestions(questions.size())
                .results(results)
                .build();
    }

    // xem chi tiết kết quả & lời giải một bài reading
    public ReadingPassageReviewDto getSubmissionReview(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        if (!"reading".equalsIgnoreCase(submission.getSkillType())) {
            throw new IllegalArgumentException("Submission is not a reading test (skillType: " + submission.getSkillType() + ")");
        }

        ReadingPassage passage = passageRepository.findById(submission.getReferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Passage not found with id: " + submission.getReferenceId()));

        Map<Long, String> userAnswers = parseAnswerData(submission.getAnswerData());
        List<ReadingQuestion> questions = passage.getQuestions();

        List<QuestionReviewDetailDto> questionReviews = (questions != null ? questions : List.<ReadingQuestion>of())
                .stream()
                .map(q -> {
                    String userAnswer = userAnswers.get(q.getId());
                    boolean isCorrect = userAnswer != null && q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase(userAnswer);
                    return QuestionReviewDetailDto.builder()
                            .questionId(q.getId())
                            .questionText(q.getQuestionText())
                            .questionType(q.getQuestionType())
                            .optionsJson(q.getOptionsJson())
                            .userAnswer(userAnswer)
                            .correctAnswer(q.getCorrectAnswer())
                            .isCorrect(isCorrect)
                            .build();
                })
                .toList();

        return ReadingPassageReviewDto.builder()
                .submissionId(submission.getId())
                .passageId(passage.getId())
                .title(passage.getTitle())
                .content(passage.getContent())
                .score(submission.getScore())
                .totalQuestions(questions != null ? questions.size() : 0)
                .createdAt(submission.getCreatedAt())
                .questions(questionReviews)
                .build();
    }

    // hàm phụ trợ parse answerData từ JSON hoặc format Map.toString()
    private Map<Long, String> parseAnswerData(String answerData) {
        Map<Long, String> result = new HashMap<>();
        if (answerData == null || answerData.isBlank()) {
            return result;
        }

        // 1. Thử parse nếu là định dạng JSON chuẩn
        try {
            return objectMapper.readValue(answerData, new TypeReference<Map<Long, String>>() {});
        } catch (Exception ignored) {
        }

        // 2. Fallback nếu là chuỗi {1=A, 2=B}
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

    // lấy toàn bộ passages trong một bài test
    public TestGroupDetaitDto getFullTest(Long testGroupId) {
        TestGroup testGroup =  testGroupRepository.findById(testGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find test group with Id: " + testGroupId));

        List<ReadingPassage> passages = passageRepository.findByTestGroupIdOrderByOrderIndexAsc(testGroupId);

        List<PassageDetailDto> passagesDto = passages.stream()
                .map(p -> PassageDetailDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .questions(mapToQuestionDto(p.getQuestions()))
                        .build())
                .toList();

        return TestGroupDetaitDto.builder()
                .testGroupId(testGroup.getId())
                .passages(passagesDto)
                .title(testGroup.getTitle())
                .build();
    }

    private List<QuestionDto> mapToQuestionDto(List<ReadingQuestion> questions) {

        return  questions.stream()
                .map(q -> QuestionDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .optionsJson(q.getOptionsJson())
                .build()).toList();
    }


}
