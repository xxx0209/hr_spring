package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private String status;
    private Integer price;
    private String comment;
    private String approverId;
    private String approverName;


    // LocalDate → LocalDateTime(자정) 변환 포함
    public Request toEntity(String loginMemberId, String loginMemberName) {
        Request r = new Request();
        r.setId(id);
        r.setMemberId(loginMemberId);   // 로그인 정보로 강제 덮어쓰기
        r.setMemberName(loginMemberName);
        r.setRequestType(requestType);
        r.setContent(content);
        r.setStartDate(startDate != null ? startDate : null);
        r.setEndDate(endDate != null ? endDate : null);
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
        dto.setStartDate(request.getStartDate() != null ? request.getStartDate() : null);
        dto.setEndDate(request.getEndDate() != null ? request.getEndDate() : null);
        dto.setDateTime(request.getDateTime());
        dto.setStatus(request.getStatus());
        dto.setPrice(request.getPrice());
        return dto;
    }
}
