package com.youpass.backend.controller;


import com.youpass.backend.dto.request.SubmitAnswersRequest;
import com.youpass.backend.dto.response.ListeningTrackDto;
import com.youpass.backend.dto.response.SubmissionListeningResultDto;
import com.youpass.backend.repository.listening.ListeningTrackRepository;
import com.youpass.backend.service.ListeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listening")
public class ListeningController {

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
            @RequestParam Long userId,
            @RequestBody SubmitAnswersRequest request
            )
    {
        return ResponseEntity.ok(listeningService.submitAnswers(userId, id, request));
    }

}
