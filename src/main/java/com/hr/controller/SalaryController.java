package com.hr.controller;

import com.hr.constant.MemberRole;
import com.hr.constant.SalaryStatus;
import com.hr.dto.MemberDto;
import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;

import com.hr.repository.MemberRepository;
import com.hr.service.MemberService;
import com.hr.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    //private final MemberRepository memberRepository;
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

        // 본인급여만 확인 할수 있게
//        MemberDto currentUser = getCurrentUser(principal);
//        List<SalaryResponseDto> salaries = salaryService.getMonthlyCompletedSalaries(currentUser.getId(), year, month);
//        return ResponseEntity.ok(salaries);


        MemberDto currentUser = getCurrentUser(principal);
        if (currentUser.getMemberRole() == MemberRole.ROLE_ADMIN || currentUser.getId().equals(memberId)) {
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

    // 급여 생성 로직
    @PostMapping
    public ResponseEntity<?> createSalary(@RequestBody SalaryRequestDto dto, Principal principal) {
        try {
            MemberDto requester = getCurrentUser(principal);
            if (requester.getMemberRole() != MemberRole.ROLE_ADMIN && !requester.getId().equals(dto.getMemberId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }

            SalaryResponseDto response = salaryService.createSalary(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            // 예외 메시지를 프론트로 전달 (400 Bad Request)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외는 서버 오류로 처리
            e.printStackTrace(); // 로그 확인용
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }


    // 4. 급여 수정 (관리자만 가능)
    @PutMapping("/{salaryId}")
    public ResponseEntity<SalaryResponseDto> updateSalary(
            @PathVariable Integer salaryId,
            @RequestBody SalaryRequestDto dto,
            Principal principal) {

        MemberDto requester = getCurrentUser(principal);
        if (requester.getMemberRole() != MemberRole.ROLE_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SalaryResponseDto response = salaryService.updateSalary(salaryId, dto);
        return ResponseEntity.ok(response);
    }

    // 5. 관리자 승인된 전체 급여 조회 (월별, 페이징)
    @GetMapping("/completed/search")
    public ResponseEntity<List<SalaryResponseDto>> searchCompletedSalaries(
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Principal principal
    ) {
        // 로그인 사용자 정보는 여전히 가져오되, 권한 체크는 제거
        MemberDto requester = getCurrentUser(principal);
//        if (requester.getMemberRole() != MemberRole.ROLE_ADMIN) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        YearMonth salaryMonth = (year != null && month != null) ? YearMonth.of(year, month) : null;
        List<SalaryResponseDto> results = salaryService.searchCompletedSalaries(memberId, salaryMonth);
        results.sort((a, b) -> b.getPayDate().compareTo(a.getPayDate())); // 최신순 정렬
        return ResponseEntity.ok(results);
    }


    // 6. 급여 상태 변경 (관리자만 가능)
    @PatchMapping("/{salaryId}/status")
    public ResponseEntity<?> updateSalaryStatus(
            @PathVariable Integer salaryId,
            @RequestParam SalaryStatus status,
            Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (status != SalaryStatus.COMPLETED && status != SalaryStatus.REJECTED && status != SalaryStatus.CANCELLED) {
            return ResponseEntity.badRequest().body("유효하지 않은 상태입니다.");
        }
//        MemberDto requester = getCurrentUser(principal);
//        if (requester.getMemberRole() != MemberRole.ROLE_ADMIN) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        salaryService.updateSalaryStatus(salaryId, status);
        return ResponseEntity.ok().build();
    }

    // 7. 승인 대기 급여 목록 조회 (DRAFT 상태만)
    @GetMapping("/pending")
    public ResponseEntity<List<SalaryResponseDto>> getPendingSalaries(Principal principal) {
        MemberDto requester = getCurrentUser(principal);
        // 관리자 전용 임시 주석처리
//        if (requester.getMemberRole() != MemberRole.ROLE_ADMIN) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        List<SalaryResponseDto> pending = salaryService.getSalariesByStatus(SalaryStatus.DRAFT);
        return ResponseEntity.ok(pending);
    }

    // 급여 삭제
    @DeleteMapping("/{salaryId}")
    public ResponseEntity<?> deleteSalary(@PathVariable Integer salaryId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        MemberDto requester = memberService.findById(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

        try {
            salaryService.deleteSalary(salaryId, requester);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

}
