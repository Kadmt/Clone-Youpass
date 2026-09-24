package com.youpass.backend.service;

import com.youpass.backend.dto.request.SubmitAnswersRequest;
import com.youpass.backend.dto.response.*;
import com.youpass.backend.entity.ListeningQuestion;
import com.youpass.backend.entity.ListeningTrack;
import com.youpass.backend.entity.Submission;
import com.youpass.backend.entity.User;
import com.youpass.backend.exception.business.ResourceNotFoundException;
import com.youpass.backend.repository.SubmissionRepository;
import com.youpass.backend.repository.UserRepository;
import com.youpass.backend.repository.listening.ListeningTrackRepository;
import org.hibernate.ResourceClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListeningService {

    @Autowired
    private ListeningTrackRepository listeningTrackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // lấy tất cả các listening tracks
    public List<ListeningTrackDto> getAllListeningTracks() {
        List<ListeningTrack> tracks = listeningTrackRepository.findAll();

        return tracks.stream().map(track -> ListeningTrackDto.builder()
                .id(track.getId())
                .title(track.getTitle())
                .build()).toList();
    }

    // lấy một bài listening
    @Transactional
    public ListeningTrackDto getListeningTrackDetail(Long id) {
        ListeningTrack track = listeningTrackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Can not find track with id: " + id ));
        List<ListeningQuestion> questions = track.getQuestions(); // OnetoMany --> use Transactional
        List<QuestionDto> mappedListeningQuestion = questions.stream().map(q -> QuestionDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .optionsJson(q.getOptionsJson())
                .build()).toList();
        return ListeningTrackDto.builder()
                .id(track.getId())
                .audioUrl(track.getAudioUrl())
                .title(track.getTitle())
                .orderIndex(track.getOrderIndex())
                .questions(mappedListeningQuestion)
                .build();
    }

    // nộp  và chấm điểm một part lẻ listening
    @Transactional(readOnly = true)
    public SubmissionListeningResultDto submitAnswers(Long userId, Long listeningTrackId, SubmitAnswersRequest answers) {
        ListeningTrack listeningTrack = listeningTrackRepository.findById(listeningTrackId).orElseThrow(() -> new ResourceNotFoundException("Can not find listening track with id: " + listeningTrackId));
        List<ListeningQuestion> questions = listeningTrack.getQuestions();

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Can not find user with ID: " + userId));

        Map<Long, String> userAnswers = (answers != null && answers.getAnswers() != null) ? answers.getAnswers() : Collections.emptyMap();

        int correctCount = 0;
        List<QuestionResultDto> results = new ArrayList<>();

        for (ListeningQuestion question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            Boolean isCorrect = userAnswer != null
                    && question.getCorrectAnswer() != null
                    && userAnswer.trim().equalsIgnoreCase(question.getCorrectAnswer().trim());

            if (isCorrect) {
                correctCount ++;
            }

            results.add(QuestionResultDto.builder()
                    .userAnswer(userAnswer)
                    .correctAnswer(question.getCorrectAnswer())
                    .questionId(question.getId())
                    .isCorrect(isCorrect)
                    .build());
        }

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setSkillType("listening");
        submission.setReferenceId(listeningTrackId);
        submission.setScore(correctCount);
        submission.setDuration(answers != null ? answers.getDuration() : null);

        try {
            submission.setAnswerData(objectMapper.writeValueAsString(userAnswers));
        } catch (Exception e) {
            submission.setAnswerData(userAnswers.toString());
        }
        submissionRepository.save(submission);

        return SubmissionListeningResultDto.builder()
                .submissionId(submission.getId())
                .score((long) correctCount)
                .duration(submission.getDuration())
                .results(results)
                .transcript(listeningTrack.getTranscript())
                .audioUrl(listeningTrack.getAudioUrl())
                .totalQuestions(questions.size())
                .build();
    }

    // lấy lịch sử các bài listening đã làm

    // tạm thời dùng userId để test Postman
    public List<SubmissionHistoryDto> getListeningSubmissionHistory(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Can not find user with Id: " + userId));
        List<Submission> submissions = submissionRepository.findByUserIdAndSkillType(userId, "listening");

        // 1. Lấy danh sách trackId duy nhất từ các submission
        List<Long> trackIds = submissions.stream()
                .map(Submission::getReferenceId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 2. Query DB 1 lần duy nhất để lấy tất cả Track và tạo Map: Key = trackId, Value = title
        Map<Long, String> trackTitleMap = listeningTrackRepository.findAllById(trackIds).stream()
                .collect(Collectors.toMap(ListeningTrack::getId, ListeningTrack::getTitle));

        return submissions.stream().map(submit -> SubmissionHistoryDto.builder()
                .id(submit.getId())
                        .createdAt(submit.getCreatedAt())
                .duration(submit.getDuration())
                .skillType("listening")
                .score(submit.getScore())
                .title(trackTitleMap.getOrDefault(submit.getReferenceId(), "Unknown Track"))
                .build())
                .toList();
    }


}
