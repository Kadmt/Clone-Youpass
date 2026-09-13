package com.youpass.backend.controller;


import com.youpass.backend.dto.response.PassageDetailDto;
import com.youpass.backend.dto.response.PassageListDto;
import com.youpass.backend.service.ReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reading")
public class ReadingController {

    @Autowired
    private ReadingService readingService;

    @GetMapping("/passages")
    public ResponseEntity<List<PassageListDto>> getAllPassages() {
        return ResponseEntity.ok(readingService.getAllPassages());
    }

    @GetMapping("/passage/{id}")
    public ResponseEntity<PassageDetailDto> getPassageDetail(@PathVariable Long id) {
        return ResponseEntity.ok(readingService.getPassageDetail(id));
    }


}
