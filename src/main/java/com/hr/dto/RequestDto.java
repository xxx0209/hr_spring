// dto/RequestDto.java
package com.hr.dto;

import com.hr.entity.Request;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class RequestDto {//extends BaseDto<Request> {

    private Long id;

    // Member.id가 String이므로 String 유지
    private String memberId;

    private String requestType;
    private String content;

    // 프론트에서 오는 "YYYY-MM-DD" 포맷 그대로 받기
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private LocalDateTime dateTime;
    private String status;

    public Request toEntity() {
        Request r = new Request();
        r.setId(id);

        // 현재 Request 엔티티가 memberId(String)만 갖고 있으므로 여기를 사용
        r.setMemberId(memberId);

        r.setRequestType(requestType);
        r.setContent(content);

        // LocalDate → LocalDateTime(자정) 변환
        r.setStartDate(startDate != null ? startDate.atStartOfDay() : null);
        r.setEndDate(endDate != null ? endDate.atStartOfDay() : null);

        r.setDateTime(dateTime != null ? dateTime : LocalDateTime.now());
        r.setStatus(status != null ? status : "작성중");
        return r;
    }

    public static RequestDto of(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setMemberId(request.getMemberId());
        dto.setRequestType(request.getRequestType());
        dto.setContent(request.getContent());
        // LocalDateTime → LocalDate (표시 용도면 필요 없고, 유지하고 싶으면 아래처럼)
        dto.setStartDate(request.getStartDate() != null ? request.getStartDate().toLocalDate() : null);
        dto.setEndDate(request.getEndDate() != null ? request.getEndDate().toLocalDate() : null);
        dto.setDateTime(request.getDateTime());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
