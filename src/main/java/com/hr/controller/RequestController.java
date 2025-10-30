package com.hr.controller;

import com.hr.dto.RequestDto;
import com.hr.entity.Request;
import com.hr.security.CustomUserDetails;
import com.hr.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    // 등록 (로그인 사용자 포함)
    @PostMapping
    public Request createRequest(@RequestBody RequestDto dto, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return requestService.save(dto, user);
    }

    // 전체 또는 사용자별 문서 조회
    @GetMapping
    public List<Request> getAll(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // 관리자면 모든 결재 문서(작성중, 승인, 반려 포함) 조회
            return requestService.findAll();
        }

        // 일반 사용자는 본인이 올린 모든 문서만 조회
        return requestService.findByMember(user.getMemberId());
    }


    @PutMapping("/{id}")
    public Request updateRequest(@PathVariable Long id, @RequestBody RequestDto dto) {
        return requestService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id) {
        requestService.delete(id);
    }

    @PatchMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestBody RequestDto dto) {
        requestService.updateStatus(id, dto.getStatus());
    }

    // 임시저장 문서만 조회
    @GetMapping("/temp")
    public List<Request> getTempRequests(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return requestService.findByMember(user.getMemberId())
                .stream()
                .filter(r -> "임시저장".equals(r.getStatus()))
                .toList();
    }

    // 결재 승인 (관리자 전용)
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        requestService.approveRequest(id, user.getName());
        return ResponseEntity.ok("결재 승인 완료");
    }

    // 결재 반려 (관리자 전용)
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        requestService.rejectRequest(id, user.getName());
        return ResponseEntity.ok("결재 반려 완료");
    }
}
