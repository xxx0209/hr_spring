package com.hr.service;

import com.hr.dto.RequestDto;
import com.hr.entity.Request;
import com.hr.repository.RequestRepository;
import com.hr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    // 저장 (로그인 사용자 포함)
    public Request save(RequestDto dto, CustomUserDetails user) {
        Request r = new Request();
        r.setMemberId(user.getMemberId());
        r.setMemberName(user.getName());
        r.setRequestType(dto.getRequestType());
        r.setContent(dto.getContent());
        r.setPrice(dto.getPrice());
        r.setStatus(dto.getStatus());
        if (dto.getStartDate() != null) r.setStartDate(dto.getStartDate().atStartOfDay());
        if (dto.getEndDate() != null) r.setEndDate(dto.getEndDate().atStartOfDay());
        r.setDateTime(LocalDateTime.now());
        return requestRepository.save(r);
    }

    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    public List<Request> findByMember(String memberId) {
        return requestRepository.findAll()
                .stream()
                .filter(r -> memberId.equals(r.getMemberId()))
                .toList();
    }

    public Request update(Long id, RequestDto dto) {
        Request existing = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기안을 찾을 수 없습니다."));

        existing.setRequestType(dto.getRequestType());
        existing.setContent(dto.getContent());
        existing.setStatus(dto.getStatus());
        existing.setPrice(dto.getPrice());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate().atStartOfDay());
        if (dto.getEndDate() != null) existing.setEndDate(dto.getEndDate().atStartOfDay());
        return requestRepository.save(existing);
    }

    public void delete(Long id) {
        requestRepository.deleteById(id);
    }

    public void updateStatus(Long id, String status) {
        Request r = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));
        r.setStatus(status);
        requestRepository.save(r);
    }

    // 결재 승인 처리
    public void approveRequest(Long id, String approverName, String comment) {
        Request r = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));
        r.setStatus("승인");
        r.setApprover(approverName);
        r.setApprovalDate(LocalDateTime.now());
        r.setComment(comment);
        requestRepository.save(r);
    }

    public void rejectRequest(Long id, String approverName, String comment) {
        Request r = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));
        r.setStatus("반려");
        r.setApprover(approverName);
        r.setApprovalDate(LocalDateTime.now());
        r.setComment(comment);
        requestRepository.save(r);
    }

}
