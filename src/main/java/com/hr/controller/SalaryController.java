package com.hr.controller;

import com.hr.constant.Role;
import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;
import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import com.hr.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final MemberRepository memberRepository;

    /**
     * 급여 생성
     */
    @PostMapping
    public ResponseEntity<SalaryResponseDto> createSalary(@RequestBody SalaryRequestDto dto) {
        SalaryResponseDto response = salaryService.createSalary(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 급여 수정
     */
    @PutMapping("/{salaryId}")
    public ResponseEntity<SalaryResponseDto> updateSalary(
            @PathVariable Integer salaryId,
            @RequestBody SalaryRequestDto dto) {
        SalaryResponseDto response = salaryService.updateSalary(salaryId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 직원별 급여 이력 조회 (권한에 따라 전체 또는 본인만)
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<SalaryResponseDto>> getSalaryHistory(
            @PathVariable String memberId,
            Principal principal) {

        // 현재 로그인한 사용자 ID
        String currentUserId = principal.getName();

        // 로그인한 사용자 정보 조회
        Member currentUser = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자 정보 없음"));

        // 관리자면 전체 조회 가능, 일반 사용자면 본인만 조회 가능
        if (currentUser.getRole() == Role.ADMIN || currentUserId.equals(memberId)) {
            List<SalaryResponseDto> history = salaryService.getSalaryHistoryByMember(memberId);
            return ResponseEntity.ok(history);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
