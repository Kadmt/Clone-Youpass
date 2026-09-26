package com.youpass.backend.service;


import com.youpass.backend.dto.response.TestGroupDto;
import com.youpass.backend.entity.TestGroup;
import com.youpass.backend.repository.TestGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestGroupService {
    @Autowired
    private TestGroupRepository testGroupRepository;

    public List<TestGroupDto> getAllReadingFullTest() {
        List<TestGroup> AllReadingFullTest = testGroupRepository.findBySkillType("reading");

        return AllReadingFullTest.stream().filter(testGroup -> "reading".equalsIgnoreCase(testGroup.getSkillType()))
                .map(testGroup -> TestGroupDto.builder()
                        .id(testGroup.getId())
                        .title(testGroup.getTitle())
                        .build()).toList();
    }

    public List<TestGroupDto> getAllListeningFullTest() {
        List<TestGroup> allListeningFullTest = testGroupRepository.findBySkillType("listening");

        return allListeningFullTest.stream().map(testGroup -> TestGroupDto.builder()
                .id(testGroup.getId())
                .title(testGroup.getTitle())
                .build()).toList();

    }

}
