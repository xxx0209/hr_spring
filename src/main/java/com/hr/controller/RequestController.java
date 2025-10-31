package com.hr.controller;

import com.hr.dto.RequestDto;
import com.hr.entity.Request;
import com.hr.entity.Member;
import com.hr.security.CustomUserDetails;
import com.hr.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    // 기안 등록
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody RequestDto dto, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(requestService.save(dto, user));
    }

    // 본인 문서 조회
    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return ResponseEntity.status(401).body("로그인 필요");
        }
        return ResponseEntity.ok(requestService.findByMember(user.getMemberId()));
    }

    // 결재현황 조회
    @GetMapping("/status")
    public ResponseEntity<?> getApprovalStatus(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(requestService.findAll().stream()
                    .filter(r -> "결재요청".equals(r.getStatus()))
                    .toList());
        } else {
            return ResponseEntity.ok(requestService.findByMember(user.getMemberId()).stream()
                    .filter(r -> "결재요청".equals(r.getStatus()))
                    .toList());
        }
    }

    // 문서 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(@PathVariable Long id, @RequestBody RequestDto dto) {
        return ResponseEntity.ok(requestService.update(id, dto));
    }

    // 문서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        requestService.delete(id);
        return ResponseEntity.ok("삭제 완료");
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody RequestDto dto) {
        requestService.updateStatus(id, dto.getStatus());
        return ResponseEntity.ok("상태 변경 완료");
    }

    // 결재 승인
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id,
                                            @RequestBody RequestDto dto,
                                            Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) return ResponseEntity.status(403).body("권한이 없습니다.");

        requestService.approveRequest(id, user.getName(), dto.getComment());
        return ResponseEntity.ok("결재 승인 완료");
    }

    // 결재 반려
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id,
                                           @RequestBody RequestDto dto,
                                           Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) return ResponseEntity.status(403).body("권한이 없습니다.");

        requestService.rejectRequest(id, user.getName(), dto.getComment());
        return ResponseEntity.ok("결재 반려 완료");
    }

    // 임시보관함 문서 조회
    @GetMapping("/temp")
    public ResponseEntity<?> getTempRequests(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        var tempList = requestService.findByMember(user.getMemberId()).stream()
                .filter(r -> "임시저장".equals(r.getStatus()))
                .toList();
        return ResponseEntity.ok(tempList);
    }

    // 결재자 목록 조회 (RequestService에서 바로 처리)
    @GetMapping("/approvers")
    public ResponseEntity<?> getApprovers() {
        return ResponseEntity.ok(requestService.findApprovers());
    }

    @GetMapping("/approvals")
    public ResponseEntity<?> getApprovalsForApprover(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        String memberId = user.getMemberId();

        Map<String, Object> result = new HashMap<>();
        result.put("requests", requestService.findRecentApprovalRequests(memberId)); // 나에게 결재요청된 문서
        result.put("processed", requestService.findRecentApprovedDocs(memberId));    // 내가 결재한 문서
        result.put("myRequests", requestService.findRecentMyRequests(memberId));     // 내가 기안한 문서

        return ResponseEntity.ok(result);
    }

}
