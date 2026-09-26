package com.youpass.backend.controller;


import com.youpass.backend.dto.request.SubmitAnswersRequest;
import com.youpass.backend.dto.response.*;
import com.youpass.backend.service.ListeningService;
import com.youpass.backend.service.TestGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listening")
public class ListeningController {

    @Autowired
    private TestGroupService testGroupService;

    @Autowired
    private ListeningService listeningService;

    @GetMapping("/tracks")
    public ResponseEntity<List<ListeningTrackDto>> getAllListeningTracks() {
        return ResponseEntity.ok(listeningService.getAllListeningTracks());
    }

    @GetMapping("/tracks/{id}")
    public ResponseEntity<ListeningTrackDto> getListeningTrackDetail(@PathVariable Long id) {
        return ResponseEntity.ok(listeningService.getListeningTrackDetail(id));
    }

    @PostMapping("/tracks/{id}/submit")
    public ResponseEntity<SubmissionListeningResultDto> submit(
            @PathVariable Long id,
            @RequestParam Long userId,  // tạm thời nhận userId để test Postman
            @RequestBody SubmitAnswersRequest request
            )
    {
        return ResponseEntity.ok(listeningService.submitAnswers(userId, id, request));
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<SubmissionHistoryDto>> getSubmissionList(@RequestParam Long userId) {
        return ResponseEntity.ok(listeningService.getListeningSubmissionHistory(userId));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<ListeningTrackReviewDto> getSubmissionReview(@PathVariable Long id) {
        return ResponseEntity.ok(listeningService.getOneListeningSubmissionResult(id));
    }

    @GetMapping("/test-groups")
    public ResponseEntity<List<TestGroupDto>> getAllListeningTestGroups() {
        return ResponseEntity.ok(testGroupService.getAllListeningFullTest());
    }

    @GetMapping("/test-groups/{testGroupId}/tracks")
    public ResponseEntity<ListeningTestGroupDetailDto> getOneListeningFullTestDetail(@PathVariable Long testGroupId) {
        return ResponseEntity.ok(listeningService.getOneFullTestListening(testGroupId));

    }
}
