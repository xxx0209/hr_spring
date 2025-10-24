// dto/RequestDto.java
package com.hr.dto;

import com.hr.entity.Member;
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
        // memberId만 넘어올 경우 직접 Member 객체를 생성
        Member member = new Member();
        member.setMemberId(memberId);

        Request request = new Request();
        request.setId(id);
        request.setMember(member);
        request.setRequestType(requestType);
        request.setContent(content);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setDateTime(dateTime != null ? dateTime : LocalDateTime.now());
        request.setStatus(status != null ? status : "작성중");
        return request;
    }

//    public static RequestDto of(Request entity) {
//        RequestDto dto = new RequestDto();
//        dto.setId(entity.getId());
//        dto.setMemberId(entity.getMember().getMemberId());
//        dto.setRequestType(entity.getRequestType());
//        dto.setContent(entity.getContent());
//        dto.setStartDate(entity.getStartDate());
//        dto.setEndDate(entity.getEndDate());
//        dto.setDateTime(entity.getDateTime());
//        dto.setStatus(entity.getStatus());
//        return dto;
//    }

    public Request careteMember() {
        return modelMapper.map(this, Request.class);
    }

    public static RequestDto of(Request request) {
        return modelMapper.map(request, RequestDto.class);
    }
}
