package com.hr.controller;

import com.hr.dto.PositionSalaryDto;
import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import com.hr.service.PositionSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<String> create(@RequestBody PositionSalaryDto dto) {
        positionSalaryService.save(dto);
        return ResponseEntity.ok("등록 성공");
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody PositionSalaryDto dto) {
        positionSalaryService.update(id, dto);
        return ResponseEntity.ok("수정 성공");
    }

    // 삭제 (비활성화 처리)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        positionSalaryService.delete(id);
        return ResponseEntity.ok("삭제 성공");
    }

    // 전체 조회 (페이징 + 검색 + 활성/비활성 필터)
    @GetMapping
    public ResponseEntity<Page<PositionSalaryDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String positionName,
            @RequestParam(required = false) Boolean active
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("position.positionName").ascending());
        Page<PositionSalaryDto> list = positionSalaryService.findAll(pageable, positionName, active);
        return ResponseEntity.ok(list);
    }

    // 회원 기준 직급급여 조회 (페이징 없이 전체)
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PositionSalaryDto>> getPositionSalariesByMember(
            @PathVariable String memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        List<PositionSalaryDto> list = positionSalaryService.getPositionSalariesForMember(member);
        return ResponseEntity.ok(list);
    }
}
