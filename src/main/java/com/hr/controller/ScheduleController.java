package com.hr.controller;

import com.hr.dto.member.ScheduleDto;
import com.hr.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Transactional
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 회원별 일정 조회 (관리자용)
    @GetMapping("/member/{memberId}")
    public List<ScheduleDto> getSchedulesByMember(@PathVariable String memberId) {
        return scheduleService.getSchedulesByMember(memberId);
    }

    // 일정 등록
    @PostMapping
    public ScheduleDto create(@RequestBody ScheduleDto dto) {
        return scheduleService.save(dto);
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public void delete(@PathVariable Long scheduleId) {
        scheduleService.delete(scheduleId);
    }
}
