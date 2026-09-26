package com.youpass.backend.service;

import com.youpass.backend.common.utils.ScoringService;
import com.youpass.backend.entity.*;
import com.youpass.backend.repository.TestGroupRepository;
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

import java.util.Collections;
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

    @Autowired
    private ScoringService scoringService;

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

        ScoringService.GradeResult gradeResult = scoringService.gradeSubmission(questions, userAnswers);

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setSkillType("reading");
        submission.setReferenceId(passageId);
        submission.setScore(gradeResult.correctCount());
        submission.setDuration(request != null ? request.getDuration() : null);

        try {
            submission.setAnswerData(objectMapper.writeValueAsString(userAnswers));
        } catch (Exception e) {
            submission.setAnswerData("{}");
        }
        submissionRepository.save(submission);

        return SubmissionReadingResultDto.builder()
                .submissionId(submission.getId())
                .score((long) gradeResult.correctCount())
                .totalQuestions(questions != null ? questions.size() : 0)
                .duration(submission.getDuration())
                .results(gradeResult.results())
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

        Map<Long, String> userAnswers = scoringService.parseAnswerData(submission.getAnswerData());
        List<ReadingQuestion> questions = passage.getQuestions();
        List<QuestionReviewDetailDto> questionReviews = scoringService.buildReviewDetails(questions, userAnswers);

        return ReadingPassageReviewDto.builder()
                .submissionId(submission.getId())
                .passageId(passage.getId())
                .title(passage.getTitle())
                .content(passage.getContent())
                .score(submission.getScore())
                .totalQuestions(questions != null ? questions.size() : 0)
                .duration(submission.getDuration())
                .createdAt(submission.getCreatedAt())
                .questions(questionReviews)
                .build();
    }

    // lấy toàn bộ passages trong một bài test
    public ReadingTestGroupDetailDto getFullTest(Long testGroupId) {
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

        return ReadingTestGroupDetailDto.builder()
                .testGroupId(testGroup.getId())
                .passages(passagesDto)
                .title(testGroup.getTitle())
                .build();
    }

    private List<QuestionDto> mapToQuestionDto(List<ReadingQuestion> questions) {
        if (questions == null) {
            return Collections.emptyList();
        }
        return  questions.stream()
                .map(q -> QuestionDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .optionsJson(q.getOptionsJson())
                .build()).toList();
    }


}
