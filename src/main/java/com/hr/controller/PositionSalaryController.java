package com.hr.controller;

import com.hr.dto.PositionSalaryDto;
import com.hr.entity.Category;
import com.hr.entity.Member;
import com.hr.entity.PositionSalary;
import com.hr.repository.MemberRepository;
import com.hr.service.PositionSalaryService;
import com.hr.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/position-salaries")
@RequiredArgsConstructor
public class PositionSalaryController {

    private final PositionSalaryService positionSalaryService;
    private final MemberRepository memberRepository;

    // 등록
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PositionSalaryDto dto) {
        positionSalaryService.save(dto);

        return ResponseEntity.ok("등록 성공");
    }

    @GetMapping
    public List<PositionSalaryDto> getAll() {
        return positionSalaryService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        positionSalaryService.delete(id);
    }

    // 직급 기준급 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PositionSalaryDto dto) {
        positionSalaryService.update(id, dto);
        return ResponseEntity.ok("수정 성공");
    }

    // 회원 기준 직급급여 조회
    // 회원 기준 직급급여 조회
    @GetMapping("/member/{memberId}")
    public List<PositionSalaryDto> getPositionSalariesByMember(@PathVariable String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return positionSalaryService.getPositionSalariesForMember(member);
    }
}
