package com.youpass.backend.service;

import com.youpass.backend.dto.request.SubmitAnswersRequest;
import com.youpass.backend.dto.response.*;
import com.youpass.backend.entity.*;
import com.youpass.backend.exception.business.ResourceNotFoundException;
import com.youpass.backend.repository.SubmissionRepository;
import com.youpass.backend.repository.TestGroupRepository;
import com.youpass.backend.repository.UserRepository;
import com.youpass.backend.repository.listening.ListeningTrackRepository;
import com.youpass.backend.common.utils.ScoringService;
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
    private TestGroupRepository testGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScoringService scoringService;

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

    // nộp và chấm điểm một part lẻ listening
    @Transactional
    public SubmissionListeningResultDto submitAnswers(Long userId, Long listeningTrackId, SubmitAnswersRequest answers) {
        ListeningTrack listeningTrack = listeningTrackRepository.findById(listeningTrackId)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find listening track with id: " + listeningTrackId));
        List<ListeningQuestion> questions = listeningTrack.getQuestions();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find user with ID: " + userId));

        Map<Long, String> userAnswers = (answers != null && answers.getAnswers() != null) ? answers.getAnswers() : Collections.emptyMap();

        ScoringService.GradeResult gradeResult = scoringService.gradeSubmission(questions, userAnswers);

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setSkillType("listening");
        submission.setReferenceId(listeningTrackId);
        submission.setScore(gradeResult.correctCount());
        submission.setDuration(answers != null ? answers.getDuration() : null);

        try {
            submission.setAnswerData(objectMapper.writeValueAsString(userAnswers));
        } catch (Exception e) {
            submission.setAnswerData(userAnswers.toString());
        }
        submissionRepository.save(submission);

        TrackInfoDto trackInfo = TrackInfoDto.builder()
                .trackId(listeningTrackId)
                .transcript(listeningTrack.getTranscript())
                .audioUrl(listeningTrack.getAudioUrl())
                .title(listeningTrack.getTitle())
                .build();

        return SubmissionListeningResultDto.builder()
                .submissionId(submission.getId())
                .score((long) gradeResult.correctCount())
                .duration(submission.getDuration())
                .results(gradeResult.results())
                .trackInfo(trackInfo)
                .totalQuestions(questions != null ? questions.size() : 0)
                .build();
    }

    // lấy lịch sử các bài listening đã làm
    // tạm thời dùng userId để test Postman
    public List<SubmissionHistoryDto> getListeningSubmissionHistory(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Can not find user with Id: " + userId));
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

    // xem chi tiết một bài listening đã nộp
    public ListeningTrackReviewDto getOneListeningSubmissionResult(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find submission with Id: " + submissionId));

        if (!"listening".equalsIgnoreCase(submission.getSkillType())) {
            throw new IllegalArgumentException("Submission is not a listening test (skillType: " + submission.getSkillType() + ")");
        }

        ListeningTrack listeningTrack = listeningTrackRepository.findById(submission.getReferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Can not find listening track with Id: " + submission.getReferenceId()));

        Map<Long, String> userAnswers = scoringService.parseAnswerData(submission.getAnswerData());
        List<ListeningQuestion> questions = listeningTrack.getQuestions();
        List<QuestionReviewDetailDto> questionReviews = scoringService.buildReviewDetails(questions, userAnswers);

        return ListeningTrackReviewDto.builder()
                .submissionId(submission.getId())
                .trackId(listeningTrack.getId())
                .title(listeningTrack.getTitle())
                .audioUrl(listeningTrack.getAudioUrl())
                .transcript(listeningTrack.getTranscript())
                .score(submission.getScore())
                .totalQuestions(questions != null ? questions.size() : 0)
                .duration(submission.getDuration())
                .createdAt(submission.getCreatedAt())
                .questions(questionReviews)
                .build();
    }

    // làm một bài full test Listening
    @Transactional(readOnly = true)
    public ListeningTestGroupDetailDto getOneFullTestListening(Long testGroupId) {
        TestGroup testGroup = testGroupRepository.findById(testGroupId).orElseThrow(() -> new ResourceNotFoundException("Can not find test group with ID: " + testGroupId));

        if (!"listening".equalsIgnoreCase(testGroup.getSkillType())) {
            throw new IllegalArgumentException("Test group ID " + testGroupId + " is not a Listening test");
        }
        List<ListeningTrack> tracks = listeningTrackRepository.findByTestGroupIdOrderByOrderIndexAsc(testGroupId);
        List<ListeningTrackDto> trackDtos = tracks.stream().map(track -> ListeningTrackDto.builder()
                .id(track.getId())
                .audioUrl(track.getAudioUrl())
                .title(track.getTitle())
                .orderIndex(track.getOrderIndex())
                .questions(mapToQuestionDto(track.getQuestions()))
                .build()
        ).toList();

        return ListeningTestGroupDetailDto.builder()
                .testGroupId(testGroupId)
                .title(testGroup.getTitle())
                .tracks(trackDtos)
                .build();


    }
    private List<QuestionDto> mapToQuestionDto(List<ListeningQuestion> questions) {
        if (questions == null) {
            return Collections.emptyList(); // sau thêm log ra màn hình lỗi
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
