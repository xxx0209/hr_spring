package com.hr.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr.dto.BaseDto;
import com.hr.entity.Request;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto extends BaseDto<Request> {

    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    private String color;
    private String content;

    @Override
    protected Class<Request> getEntityClass() {
        return Request.class;
    }

}
