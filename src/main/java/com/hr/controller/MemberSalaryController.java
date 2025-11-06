package com.hr.controller;

import com.hr.dto.MemberSalaryDto;
import com.hr.dto.PositionSalaryDto;
import com.hr.entity.MemberSalary;
import com.hr.service.MemberSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-salaries")
@RequiredArgsConstructor
public class MemberSalaryController {

    private final MemberSalaryService memberSalaryService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MemberSalaryDto dto) {
        memberSalaryService.save(dto);
        return ResponseEntity.ok("등록 성공");
    }

    @GetMapping  // 기본급 등록 전체 회원 조회
    public List<MemberSalaryDto> getAll(){
        return memberSalaryService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        memberSalaryService.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MemberSalaryDto dto) {
        memberSalaryService.update(id, dto);
        return ResponseEntity.ok("수정 성공");
    }

}
