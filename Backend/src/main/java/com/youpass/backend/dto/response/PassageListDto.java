package com.youpass.backend.dto.response;


import com.youpass.backend.entity.TestGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder // auto generate constructor
public class PassageListDto {
    private Long id;
    private String title;
}
