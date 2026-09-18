package com.youpass.backend.controller;


import com.youpass.backend.dto.response.SubmissionHistoryDto;
import com.youpass.backend.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @GetMapping()
    public ResponseEntity<List<SubmissionHistoryDto>> getSubmissions(@RequestParam Long userId, @RequestParam(required = false) String skillType)  {
        List<SubmissionHistoryDto> result = submissionService.getAllSubmissions(userId, skillType);
        return ResponseEntity.ok(result);

    }
    // require = false --> do user có thể xem toàn bộ submission hoặc một skill riêng lẻ do vậy skillType không bắt buộc phải có
}
