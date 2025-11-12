package com.hr.controller;

import com.hr.dto.SalaryResponseDto;
import com.hr.security.CustomUserDetails;
import com.hr.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    // 급여 등록 (기본 상태 : DRAFT, 지급일: 20일 고정)
    @PostMapping
    public ResponseEntity<SalaryResponseDto> create(@RequestBody SalaryResponseDto dto) {
        return ResponseEntity.ok(salaryService.create(dto));
    }

    // 급여 수정
    @PutMapping("/{salaryId}")
    public ResponseEntity<SalaryResponseDto> updateSalary(
            @PathVariable Integer salaryId,
            @RequestBody SalaryResponseDto dto) {
        SalaryResponseDto updatedSalary = salaryService.updateAndRecalculate(salaryId, dto);
        return ResponseEntity.ok(updatedSalary);
    }

    // 급여 승인
    @PostMapping("/{salaryId}/approve")
    public ResponseEntity<SalaryResponseDto> approve(@PathVariable Integer salaryId) {
        return ResponseEntity.ok(salaryService.approve(salaryId));
    }

    // 전체 급여 조회 (페이징 + 검색)
    @GetMapping
    public ResponseEntity<Page<SalaryResponseDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchMemberName,
            @RequestParam(required = false) String salaryMonth
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("salaryMonth").descending());
        Page<SalaryResponseDto> salaries = salaryService.findAll(pageable, searchMemberName, salaryMonth);
        return ResponseEntity.ok(salaries);
    }
    // 미승인 급여 조회 (DRAFT, 페이징 + 검색)
    @GetMapping("/drafts")
    public ResponseEntity<Page<SalaryResponseDto>> findDraftSalaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("salaryMonth").descending());
        Page<SalaryResponseDto> salaries = salaryService.findDraftSalaries(pageable, search);
        return ResponseEntity.ok(salaries);
    }

    // 완료 급여 조회 (COMPLETED, 페이징 + 검색 + 필터)
    @GetMapping("/completed")
    public ResponseEntity<Page<SalaryResponseDto>> getCompletedSalaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) String salaryMonth
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("salaryMonth").descending());
        Page<SalaryResponseDto> salaries = salaryService.findCompletedSalariesFiltered(pageable, memberId, salaryMonth);
        return ResponseEntity.ok(salaries);
    }

    // 급여 삭제
    @DeleteMapping("/{salaryId}")
    public ResponseEntity<String> deleteSalary(@PathVariable Integer salaryId) {
        try {
            salaryService.delete(salaryId);
            return ResponseEntity.ok("급여가 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인한 직원의 급여 내역 조회 (페이징 없이 전체)
    @GetMapping("/me")
    public ResponseEntity<List<SalaryResponseDto>> getMySalaries(
            @RequestParam String memberId
    ) {
        List<SalaryResponseDto> salaries = salaryService.findByMemberId(memberId);
        return ResponseEntity.ok(salaries);
    }

    // 로그인한 직원의 특정 급여 상세 조회
    @GetMapping("/me/{salaryId}")
    public ResponseEntity<SalaryResponseDto> getMySalaryDetail(
            @PathVariable Integer salaryId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = userDetails.getMemberId();
        return ResponseEntity.ok(salaryService.findMySalaryDetail(memberId, salaryId));
    }
}
