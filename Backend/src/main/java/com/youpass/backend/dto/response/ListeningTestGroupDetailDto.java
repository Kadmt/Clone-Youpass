package com.youpass.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;


@Getter
@Builder
public class ListeningTestGroupDetailDto {
    private Long testGroupId;
    private String title;
    private List<ListeningTrackDto> tracks;
}
