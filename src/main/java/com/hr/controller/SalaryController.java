package com.hr.controller;

import com.hr.constant.Role;
import com.hr.constant.SalaryStatus;
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

    // 🔹 공통 사용자 조회
    private Member getCurrentUser(Principal principal) {
        return memberRepository.findById(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자 정보 없음"));
    }

    // 1. 월별 급여 내역 조회 (본인 또는 관리자)
    @GetMapping("/member/{memberId}/monthly")
    public ResponseEntity<List<SalaryResponseDto>> getMonthlySalaries(
            @PathVariable String memberId,
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {

        Member currentUser = getCurrentUser(principal);
        if (currentUser.getRole() == Role.ADMIN || currentUser.getId().equals(memberId)) {
            List<SalaryResponseDto> salaries = salaryService.getMonthlySalaries(memberId, year, month);
            return ResponseEntity.ok(salaries);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // 2. 급여 단건 상세 조회
    @GetMapping("/{salaryId}")
    public ResponseEntity<SalaryResponseDto> getSalaryDetail(@PathVariable Integer salaryId, Principal principal) {
        SalaryResponseDto dto = salaryService.getSalaryDetail(salaryId, principal.getName());
        return ResponseEntity.ok(dto);
    }

    // 3. 급여 생성
    @PostMapping
    public ResponseEntity<SalaryResponseDto> createSalary(@RequestBody SalaryRequestDto dto, Principal principal) {
        Member requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN && !requester.getId().equals(dto.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SalaryResponseDto response = salaryService.createSalary(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 4. 급여 수정
    @PutMapping("/{salaryId}")
    public ResponseEntity<SalaryResponseDto> updateSalary(
            @PathVariable Integer salaryId,
            @RequestBody SalaryRequestDto dto,
            Principal principal) {

        Member requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SalaryResponseDto response = salaryService.updateSalary(salaryId, dto);
        return ResponseEntity.ok(response);
    }

    // 5. 관리자 전체 급여 조회 (월별)
    @GetMapping("/all")
    public ResponseEntity<List<SalaryResponseDto>> getAllSalariesByMonth(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {

        Member requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<SalaryResponseDto> salaries = salaryService.getAllSalariesByMonth(year, month);
        return ResponseEntity.ok(salaries);
    }

    // 6. 급여 상태 변경
    @PatchMapping("/{salaryId}/status")
    public ResponseEntity<Void> updateSalaryStatus(
            @PathVariable Integer salaryId,
            @RequestParam SalaryStatus status,
            Principal principal) {

        Member requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        salaryService.updateSalaryStatus(salaryId, status);
        return ResponseEntity.ok().build();
    }

    // 7. 승인 대기 급여 목록 조회
    @GetMapping("/pending")
    public ResponseEntity<List<SalaryResponseDto>> getPendingSalaries(Principal principal) {
        Member requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<SalaryResponseDto> pending = salaryService.getSalariesByStatus(SalaryStatus.DRAFT);
        return ResponseEntity.ok(pending);
    }
}
