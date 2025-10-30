package com.hr.controller;

import com.hr.constant.BaseSalaryType;
import com.hr.dto.BaseSalaryDto;
import com.hr.entity.BaseSalary;
import com.hr.repository.BaseSalaryRepository;
import com.hr.service.BaseSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base-salaries")
@RequiredArgsConstructor
public class BaseSalaryController {

    private final BaseSalaryRepository baseSalaryRepository;
    private final BaseSalaryService baseSalaryService;

    // 기준급 조회
    @GetMapping
    public List<BaseSalary> getBaseSalaries(@RequestParam BaseSalaryType type) {
        return baseSalaryRepository.findByType(type);
    }

    // 기준급 등록
    @PostMapping
    public ResponseEntity<BaseSalary> createBaseSalary(@RequestBody BaseSalaryDto dto) {
        BaseSalary saved = baseSalaryService.registerBaseSalary(dto);
        return ResponseEntity.ok(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<BaseSalary> updateBaseSalary(@PathVariable Long id, @RequestBody BaseSalaryDto dto) {
        BaseSalary updated = baseSalaryService.updateBaseSalary(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBaseSalary(@PathVariable Long id) {
        baseSalaryService.deleteBaseSalary(id);
        return ResponseEntity.noContent().build();
    }

}
