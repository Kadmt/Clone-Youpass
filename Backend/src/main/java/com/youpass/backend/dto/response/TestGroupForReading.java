package com.youpass.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Builder
@Setter
public class TestGroupForReading {
    private Long id;
    private String title;
}
