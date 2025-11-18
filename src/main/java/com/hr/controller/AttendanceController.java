package com.hr.controller;

import com.hr.dto.AttendanceDto;
import com.hr.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 출퇴근 상태 조회 API
    @GetMapping("/status")
    public String getAttendanceState() {
        return attendanceService.getAttendanceState();
    }

    // 출퇴근 기록 조회
    @GetMapping("/list")
    public List<AttendanceDto> getAttendanceList(@RequestParam int page,
                                                 @RequestParam int size) {
        // 출퇴근 기록 조회
        return attendanceService.getAttendanceList(page, size);
    }

    //출근 체크
    @PostMapping("/checkin")
    public ResponseEntity<String> checkin() {
        try {
            attendanceService.checkIn();
            return ResponseEntity.ok("출근 체크 완료");
        }catch(IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //퇴근 체크
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout() {
        try {
            attendanceService.checkOut();
            return ResponseEntity.ok("퇴근 체크 완료");
        }catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
