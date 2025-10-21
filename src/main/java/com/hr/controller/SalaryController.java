package com.hr.controller;

import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;
import com.hr.service.SalaryService;
import com.hr.service.SalaryPaymentService;
import com.hr.service.SalaryListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;               // 급여 계산 및 저장
    private final SalaryPaymentService salaryPaymentService; // 급여 지급 처리
    private final SalaryListService salaryListService;       // 급여 조회

    /**
     * 급여 계산 및 저장
     * POST /api/salaries/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<SalaryResponseDto> calculateSalary(@RequestBody SalaryRequestDto requestDto) {
        SalaryResponseDto response = salaryService.calculateAndSaveSalary(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 급여 지급 처리
     * POST /api/salaries/{salaryId}/pay
     */
    @PostMapping("/{salaryId}/pay")
    public ResponseEntity<SalaryResponseDto> markAsPaid(@PathVariable Integer salaryId) {
        SalaryResponseDto response = salaryPaymentService.markSalaryAsPaid(salaryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인한 사용자의 급여 내역 전체 조회
     * GET /api/salaries/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<SalaryResponseDto>> getMySalaries(@RequestHeader("X-User-Id") String memberId) {
        List<SalaryResponseDto> responseList = salaryListService.getSalariesForMember(memberId);
        return ResponseEntity.ok(responseList);
    }

    /**
     * 로그인한 사용자의 급여 내역 기간별 조회
     * GET /api/salaries/my/period?startDate=2025-01-01&endDate=2025-10-31
     */
    @GetMapping("/my/period")
    public ResponseEntity<List<SalaryResponseDto>> getMySalariesByPeriod(
            @RequestHeader("X-User-Id") String memberId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<SalaryResponseDto> responseList = salaryListService.getSalariesByPeriod(memberId, start, end);
        return ResponseEntity.ok(responseList);
    }
}
