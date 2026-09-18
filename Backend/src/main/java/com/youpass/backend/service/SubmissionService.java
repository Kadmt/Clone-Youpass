package com.youpass.backend.service;


import com.youpass.backend.dto.response.SubmissionHistoryDto;
import com.youpass.backend.entity.ListeningTrack;
import com.youpass.backend.entity.ReadingPassage;
import com.youpass.backend.entity.Submission;
import com.youpass.backend.entity.WritingPrompt;
import com.youpass.backend.repository.SubmissionRepository;
import com.youpass.backend.repository.listening.ListeningTrackRepository;
import com.youpass.backend.repository.reading.ReadingPassageRepository;
import com.youpass.backend.repository.writing.WritingPromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ReadingPassageRepository readingPassageRepository;

    @Autowired
    private WritingPromptRepository writingPromptRepository;

    @Autowired
    private ListeningTrackRepository listeningTrackRepository;

    // lấy danh sách các submission
    public List<SubmissionHistoryDto>  getAllSubmissions(Long userId, String skillType) {
        List<Submission> submissionList = (skillType == null) ? submissionRepository.findByUserId(userId) : submissionRepository.findByUserIdAndSkillType(userId, skillType);
        List<SubmissionHistoryDto> result = new ArrayList<>();

        for (Submission submission : submissionList) {
            result.add(SubmissionHistoryDto.builder()
                    .id(submission.getId())
                    .createdAt(submission.getCreatedAt())
                    .score(submission.getScore())
                    .skillType(submission.getSkillType())
                    .title(resolveTitle(submission))
                    .build()
            );
        }
        return result;
    }

    // hàm con lấy title dựa trên submission
    private String resolveTitle(Submission s) {
        Long referenceId = s.getReferenceId();
        String skillType = s.getSkillType().toLowerCase();

        return switch (skillType) {
            case "reading" -> readingPassageRepository.findById(referenceId)
                    .map(ReadingPassage::getTitle)
                    .orElse("Unknown");
            case "writing" -> writingPromptRepository.findById(referenceId)
                    .map(WritingPrompt::getTitle)
                    .orElse("Unknown");
            case "listening" -> listeningTrackRepository.findById(referenceId)
                    .map(ListeningTrack::getTitle)
                    .orElse("Unknown");
            default -> "Unknown";
        };
    }

    }


