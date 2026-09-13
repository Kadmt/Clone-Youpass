package com.youpass.backend.service;

import com.youpass.backend.dto.response.PassageDetailDto;
import com.youpass.backend.dto.response.PassageListDto;
import com.youpass.backend.dto.response.QuestionDto;
import com.youpass.backend.entity.ReadingPassage;
import com.youpass.backend.exception.business.ResourceNotFoundException;
import com.youpass.backend.repository.reading.ReadingPassageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingService {

    @Autowired
    private ReadingPassageRepository passageRepository;

    public List<PassageListDto> getAllPassages() {
        List<ReadingPassage> passages = passageRepository.findAll();

        return passages.stream()
                .map(passage -> PassageListDto.builder()
                        .id(passage.getId())
                        .title(passage.getTitle())
                        .build())
                .toList();
    }

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



}
