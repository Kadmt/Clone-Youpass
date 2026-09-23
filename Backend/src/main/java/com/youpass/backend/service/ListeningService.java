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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Transactional
    public SubmissionListeningResultDto submitAnswers(Long userId, Long listeningTrackId, SubmitAnswersRequest answers) {
        ListeningTrack listeningTrack = listeningTrackRepository.findById(listeningTrackId).orElseThrow(() -> new ResourceNotFoundException("Can not find listening track with id: " + listeningTrackId));
        List<ListeningQuestion> questions = listeningTrack.getQuestions();

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceClosedException("Can not find user with ID: " + userId));

        Map<Long, String> userAnswers = answers.getAnswers() != null ? answers.getAnswers() : new HashMap<>();

        int correctCount = 0;
        List<QuestionResultDto> results = new ArrayList<>();

        for (ListeningQuestion question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            Boolean isCorrect = userAnswer != null && userAnswer.equalsIgnoreCase(question.getCorrectAnswer());

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

        try {
            submission.setAnswerData(objectMapper.writeValueAsString(userAnswers));
        } catch (Exception e) {
            submission.setAnswerData(userAnswers.toString());
        }
        submissionRepository.save(submission);


        return SubmissionListeningResultDto.builder()
                .submissionId(submission.getId())
                .score((long) correctCount)
                .results(results)
                .transcript(listeningTrack.getTranscript())
                .audioUrl(listeningTrack.getAudioUrl())
                .totalQuestions(questions.size())
                .build();
    }


}
