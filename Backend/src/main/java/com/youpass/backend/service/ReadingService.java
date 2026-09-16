package com.youpass.backend.service;

import com.youpass.backend.dto.request.SubmitAnswersRequest;
import com.youpass.backend.dto.response.*;
import com.youpass.backend.entity.ReadingPassage;
import com.youpass.backend.entity.ReadingQuestion;
import com.youpass.backend.entity.Submission;
import com.youpass.backend.entity.User;
import com.youpass.backend.exception.business.ResourceNotFoundException;
import com.youpass.backend.repository.SubmissionRepository;
import com.youpass.backend.repository.UserRepository;
import com.youpass.backend.repository.reading.ReadingPassageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReadingService {

    @Autowired
    private ReadingPassageRepository passageRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;


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

    // chi tiết đề kèm câu hỏi ( không chứa đáp án)
    public PassageDetailDto getPassageDetail(Long id) {
        ReadingPassage passage = passageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Passage not found"));

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

    //nộp bài chấm điểm
    public SubmissionResultDto submitAnswers(Long userId, Long passageId, SubmitAnswersRequest request ) {
        ReadingPassage passage = passageRepository.findById(passageId)
                .orElseThrow(() -> new ResourceNotFoundException("Passage not found "));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ReadingQuestion> questions = passage.getQuestions();
        Map<Long, String> userAnswers = request.getAnswers();

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
        submission.setAnswerData(userAnswers.toString());

        submissionRepository.save(submission);

        return SubmissionResultDto.builder()
                .submissionId(submission.getId())
                .score((long) correctCount)
                .totalQuestions(questions.size())
                .results(results)
                .build();



}
}
