package com.hr.controller;

import com.hr.constant.MemberRole;
import com.hr.constant.SalaryStatus;
import com.hr.dto.MemberDto;
import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;
import com.hr.security.CustomUserDetails;
import com.hr.service.MemberService;
import com.hr.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    // 급여 등록(기본 상태 : DRAFT, 지급일: 20일 고정)
    @PostMapping
    public ResponseEntity<SalaryResponseDto> create(@RequestBody SalaryResponseDto dto) {
        return ResponseEntity.ok(salaryService.create(dto));
    }

   //  급여 수정
    @PutMapping("/{salaryId}")
    public ResponseEntity<SalaryResponseDto> update(@PathVariable Integer salaryId,
                                                    @RequestBody SalaryResponseDto dto) {
        return ResponseEntity.ok(salaryService.update(salaryId, dto));
    }

    // 급여 승인
    @PostMapping("/{salaryId}/approve")
    public ResponseEntity<SalaryResponseDto> approve(@PathVariable Integer salaryId) {
        return ResponseEntity.ok(salaryService.approve(salaryId));
    }

    // 4. 전체 급여 조회
    @GetMapping
    public ResponseEntity<List<SalaryResponseDto>> findAll() {
        return ResponseEntity.ok(salaryService.findAll());
    }

    // 5. 미승인 급여 조회(DRAFT)
    @GetMapping("/drafts")
    public ResponseEntity<List<SalaryResponseDto>> findDraftSalaries() {
        return ResponseEntity.ok(salaryService.findDraftSalaries());
    }

    // 승인한 급여 조회
    @GetMapping("/completed")
    public ResponseEntity<List<SalaryResponseDto>> getCompletedSalaries() {
        return ResponseEntity.ok(salaryService.findCompletedSalaries());
    }

    /**
     * 급여 삭제
     * - 이미 지급(COMPLETED) 상태이면 삭제 불가
     */
    @DeleteMapping("/{salaryId}")
    public ResponseEntity<String> deleteSalary(@PathVariable Integer salaryId) {
        try {
            salaryService.delete(salaryId);
            return ResponseEntity.ok("급여가 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인한 직원의 급여 내역 조회
    @GetMapping("/me")
    public ResponseEntity<List<SalaryResponseDto>> getMySalaries(
            @RequestParam String memberId
    ) {
        return ResponseEntity.ok(salaryService.findByMemberId(memberId));
    }

    // 로그인한 직원의 특정 급여 상세 조회
    @GetMapping("/me/{salaryId}")
    public ResponseEntity<SalaryResponseDto> getMySalaryDetail(
            @PathVariable Integer salaryId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 로그인된 직원의 ID를 사용하여 급여 상세 조회
        String memberId = userDetails.getMemberId();
        return ResponseEntity.ok(salaryService.findMySalaryDetail(memberId, salaryId));
    }
}
