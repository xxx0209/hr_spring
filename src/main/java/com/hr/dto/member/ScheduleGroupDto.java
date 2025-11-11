package com.hr.dto.member;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleGroupDto {
    List<ScheduleDto> scheduleDtoList = new ArrayList<>();
    List<RequestDto> requestDtoList = new ArrayList<>();
}
