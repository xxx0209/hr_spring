package com.hr.controller;

import com.hr.constant.Role;
import com.hr.constant.SalaryStatus;
import com.hr.dto.MemberDto;
import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;
import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import com.hr.service.MemberService;
import com.hr.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final MemberRepository memberRepository;
    private final MemberService memberService ;

    // 🔹 공통 사용자 조회
    private MemberDto getCurrentUser(Principal principal) {
        return memberService.findById(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자 정보 없음"));
    }


    // 1. 월별 급여 내역 조회 (본인 또는 관리자) - 승인된 급여만
    @GetMapping("/member/{memberId}/monthly")
    public ResponseEntity<List<SalaryResponseDto>> getMonthlySalaries(
            @PathVariable String memberId,
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {

        MemberDto currentUser = getCurrentUser(principal);
        if (currentUser.getRole() == Role.ADMIN || currentUser.getId().equals(memberId)) {
            List<SalaryResponseDto> salaries = salaryService.getMonthlyCompletedSalaries(memberId, year, month);
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

    // 3. 급여 생성 (관리자 또는 본인만 가능)
    @PostMapping
    public ResponseEntity<SalaryResponseDto> createSalary(@RequestBody SalaryRequestDto dto, Principal principal) {
        MemberDto requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN && !requester.getId().equals(dto.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SalaryResponseDto response = salaryService.createSalary(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 4. 급여 수정 (관리자만 가능)
    @PutMapping("/{salaryId}")
    public ResponseEntity<SalaryResponseDto> updateSalary(
            @PathVariable Integer salaryId,
            @RequestBody SalaryRequestDto dto,
            Principal principal) {

        MemberDto requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SalaryResponseDto response = salaryService.updateSalary(salaryId, dto);
        return ResponseEntity.ok(response);
    }

    // 5. 관리자 전체 급여 조회 (월별, 페이징)
    @GetMapping("/all")
    public ResponseEntity<Page<SalaryResponseDto>> getAllSalariesByMonth(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        MemberDto requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        YearMonth salaryMonth = YearMonth.of(year, month);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(salaryService.getPagedAllSalariesByMonth(salaryMonth, pageable));
    }

    // 6. 급여 상태 변경 (관리자만 가능)
    @PatchMapping("/{salaryId}/status")
    public ResponseEntity<Void> updateSalaryStatus(
            @PathVariable Integer salaryId,
            @RequestParam SalaryStatus status,
            Principal principal) {

        MemberDto requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        salaryService.updateSalaryStatus(salaryId, status);
        return ResponseEntity.ok().build();
    }

    // 7. 승인 대기 급여 목록 조회 (DRAFT 상태만)
    @GetMapping("/pending")
    public ResponseEntity<List<SalaryResponseDto>> getPendingSalaries(Principal principal) {
        MemberDto requester = getCurrentUser(principal);
        if (requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<SalaryResponseDto> pending = salaryService.getSalariesByStatus(SalaryStatus.DRAFT);
        return ResponseEntity.ok(pending);
    }
}
