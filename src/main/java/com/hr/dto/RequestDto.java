// 📁 dto/RequestDto.java
package com.hr.dto;

import com.hr.entity.Request;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestDto extends BaseDto {

    private Long id;
    private String memberId;
    private String requestType;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime dateTime;
    private String status;

    public Request toEntity() {
        return modelMapper.map(this, Request.class);
    }

    public static RequestDto of(Request entity) {
        return modelMapper.map(entity, RequestDto.class);
    }
}
