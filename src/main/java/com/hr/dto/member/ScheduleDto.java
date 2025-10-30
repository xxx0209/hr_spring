package com.hr.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr.dto.BaseDto;
import com.hr.entity.Member;
import com.hr.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "제목은 필수 입력 사항입니다.")
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    private String color;

    @NotBlank(message = "내용은 필수 입력 사항입니다.")
    private String content;

    @Override
    protected Class<Schedule> getEntityClass() {
        return Schedule.class;
    }
}
