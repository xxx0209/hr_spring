// 📁 dto/RequestDto.java
package com.hr.dto;

import com.hr.entity.Request;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestDto extends BaseDto<Request> {

    private Long id;
    private String memberId;
    private String requestType;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime dateTime;
    private String status;

    @Override
    protected Class<Request> getEntityClass() {
        return Request.class;
    }
}
