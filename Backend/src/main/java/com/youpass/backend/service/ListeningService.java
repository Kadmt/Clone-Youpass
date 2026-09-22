package com.youpass.backend.service;

import com.youpass.backend.dto.response.ListeningTrackDto;
import com.youpass.backend.dto.response.QuestionDto;
import com.youpass.backend.entity.ListeningQuestion;
import com.youpass.backend.entity.ListeningTrack;
import com.youpass.backend.exception.business.ResourceNotFoundException;
import com.youpass.backend.repository.listening.ListeningTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListeningService {

    @Autowired
    private ListeningTrackRepository listeningTrackRepository;

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


}
