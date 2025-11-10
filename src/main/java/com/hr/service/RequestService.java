package com.hr.service;

import com.hr.dto.RequestDto;
import com.hr.entity.Request;
import com.hr.repository.MemberRepository;
import com.hr.repository.RequestRepository;
import com.hr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final MemberRepository memberRepository; // 결재자 조회용

    //  기안 저장
    public Request save(RequestDto dto, CustomUserDetails user) {
        Request r = new Request();
        r.setMemberId(user.getMemberId());
        r.setMemberName(user.getName());
        r.setApproverId(dto.getApproverId());
        r.setApproverName(dto.getApproverName());
        r.setRequestType(dto.getRequestType());
        r.setContent(dto.getContent());
        r.setPrice(dto.getPrice());
        r.setStatus(dto.getStatus() != null ? dto.getStatus() : "결재요청");
        if (dto.getStartDate() != null) r.setStartDate(dto.getStartDate().atStartOfDay());
        if (dto.getEndDate() != null) r.setEndDate(dto.getEndDate().atStartOfDay());
        r.setDateTime(LocalDateTime.now());
        return requestRepository.save(r);
    }

    //  전체 조회
    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    //  사용자별 문서 전체 조회
    public List<Request> findByMember(String memberId) {
        return requestRepository.findAll().stream()
                .filter(r -> memberId.equals(r.getMemberId()))
                .toList();
    }

    //  문서 수정
    public Request update(Long id, RequestDto dto) {
        Request existing = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기안을 찾을 수 없습니다."));
        existing.setRequestType(dto.getRequestType());
        existing.setContent(dto.getContent());
        existing.setStatus(dto.getStatus());
        existing.setPrice(dto.getPrice());
        existing.setApproverId(dto.getApproverId());
        existing.setApproverName(dto.getApproverName());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate().atStartOfDay());
        if (dto.getEndDate() != null) existing.setEndDate(dto.getEndDate().atStartOfDay());
        return requestRepository.save(existing);
    }

    //  삭제
    public void delete(Long id) {
        requestRepository.deleteById(id);
    }

    //  상태 변경
    public void updateStatus(Long id, String status) {
        Request r = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));
        r.setStatus(status);
        requestRepository.save(r);
    }

    //  승인
    public void approveRequest(Long id, String approverName, String comment) {
        Request r = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));
        r.setStatus("승인");
        r.setApproverName(approverName);
        r.setApprovalDate(LocalDateTime.now());
        r.setComment(comment);
        requestRepository.save(r);
    }

    // 반려
    public void rejectRequest(Long id, String approverName, String comment) {
        Request r = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));
        r.setStatus("반려");
        r.setApproverName(approverName);
        r.setApprovalDate(LocalDateTime.now());
        r.setComment(comment);
        requestRepository.save(r);
    }

    //  결재자 목록 조회 (ROLE_ADMIN만 필터링)
    public List<Map<String, String>> findApprovers() {
        return memberRepository.findAll().stream()
                .filter(m -> m.getMemberRole().name().equals("ROLE_ADMIN"))
                .map(m -> Map.of("memberId", m.getId(), "name", m.getName()))
                .toList();
    }

    // [관리자용] 전체 결재 요청 문서
    public List<Request> findApprovalRequests(String approverId) {
        return requestRepository.findAll().stream()
                .filter(r -> approverId.equals(r.getApproverId()) && "결재요청".equals(r.getStatus()))
                .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
                .toList();
    }

    // [관리자용] 전체 결재 처리 문서
    public List<Request> findApprovedDocs(String approverId) {
        return requestRepository.findAll().stream()
                .filter(r -> approverId.equals(r.getApproverId()) &&
                        ("승인".equals(r.getStatus()) || "반려".equals(r.getStatus())))
                .sorted((a, b) -> {
                    if (a.getApprovalDate() == null || b.getApprovalDate() == null) return 0;
                    return b.getApprovalDate().compareTo(a.getApprovalDate());
                })
                .toList();
    }

    // [사용자용] 전체 내 기안 문서
    public List<Request> findMyRequests(String memberId) {
        return requestRepository.findAll().stream()
                .filter(r -> memberId.equals(r.getMemberId()))
                .filter(r -> !"임시저장".equals(r.getStatus())) // 임시저장 제외
                .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
                .toList();
    }
}
