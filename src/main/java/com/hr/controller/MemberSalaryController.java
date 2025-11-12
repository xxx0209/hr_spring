package com.hr.controller;

import com.hr.dto.MemberSalaryDto;
import com.hr.service.MemberSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member-salaries")
@RequiredArgsConstructor
public class MemberSalaryController {

    private final MemberSalaryService memberSalaryService;

    // 등록
    @PostMapping
    public ResponseEntity<String> create(@RequestBody MemberSalaryDto dto) {
        memberSalaryService.save(dto);
        return ResponseEntity.ok("등록 성공");
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody MemberSalaryDto dto) {
        memberSalaryService.update(id, dto);
        return ResponseEntity.ok("수정 성공");
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        memberSalaryService.delete(id);
        return ResponseEntity.ok("삭제 성공");
    }

    // 전체 조회 (페이징 + 검색)
    @GetMapping
    public ResponseEntity<Page<MemberSalaryDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberName").ascending());
        Page<MemberSalaryDto> list = memberSalaryService.findAll(pageable, search);
        return ResponseEntity.ok(list);
    }
}
