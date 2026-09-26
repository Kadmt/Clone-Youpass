package com.youpass.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TestGroupDetaitDto {
    private Long testGroupId;
    private String title;
    private List<PassageDetailDto> passages;
}
