package com.hr.dto;

import com.hr.entity.Request;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class RequestDto extends BaseDto<Request> {

    private Long id;
    private String memberId;
    private String memberName;
    private String requestType;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime dateTime;
    private String status;
    private Integer price;

    // LocalDate → LocalDateTime(자정) 변환 포함
    public Request toEntity(String loginMemberId, String loginMemberName) {
        Request r = new Request();
        r.setId(id);
        r.setMemberId(loginMemberId);   // 로그인 정보로 강제 덮어쓰기
        r.setMemberName(loginMemberName);
        r.setRequestType(requestType);
        r.setContent(content);
        r.setStartDate(startDate != null ? startDate.atStartOfDay() : null);
        r.setEndDate(endDate != null ? endDate.atStartOfDay() : null);
        r.setDateTime(LocalDateTime.now());
        r.setStatus(status != null ? status : "작성중");
        r.setPrice(price);
        return r;
    }


    @Override
    protected Class<Request> getEntityClass() {
        return Request.class;
    }

    // LocalDateTime → LocalDate (프론트로 내려보낼 때 변환)
    public static RequestDto of(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setMemberId(request.getMemberId());
        dto.setMemberName(request.getMemberName());
        dto.setRequestType(request.getRequestType());
        dto.setContent(request.getContent());
        dto.setStartDate(request.getStartDate() != null ? request.getStartDate().toLocalDate() : null);
        dto.setEndDate(request.getEndDate() != null ? request.getEndDate().toLocalDate() : null);
        dto.setDateTime(request.getDateTime());
        dto.setStatus(request.getStatus());
        dto.setPrice(request.getPrice());
        return dto;
    }
}
