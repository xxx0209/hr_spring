package com.hr.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr.dto.BaseDto;
import com.hr.entity.Member;
import com.hr.entity.Schedule;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDto extends BaseDto<Schedule> {
    private Long scheduleId;
    private String memberId;
    private Long categoryId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;

    private String color;

    @Override
    protected Class<Schedule> getEntityClass() {
        return Schedule.class;
    }
}
