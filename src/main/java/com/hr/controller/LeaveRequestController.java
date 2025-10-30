package com.hr.controller;

import com.hr.dto.LeaveRequestDto;
import com.hr.entity.LeaveRequest;
import com.hr.entity.LeaveStatus;
import com.hr.repository.LeaveRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestController(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @PostMapping("/leave")
    public ResponseEntity<?> requestLeave(@RequestBody LeaveRequestDto dto) {
        // TODO: 인증 기능 완성되면 SecurityContext에서 userId 가져오기
        // TODO: 결재 권한 분기 로직 추가 예정
        // TODO: 예외 처리 로직 연결 필요
        LeaveRequest leave = new LeaveRequest();
        leave.setUserId(dto.getUserId()); //임시 처리
        leave.setLeaveType(dto.getLeaveType());
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING); // 대기 상태 설정

        leaveRequestRepository.save(leave);

        // 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("id", leave.getId());
        response.put("userId", leave.getUserId());
        response.put("leaveType", leave.getLeaveType());
        response.put("startDate", leave.getStartDate());
        response.put("endDate", leave.getEndDate());
        response.put("reason", leave.getReason());
        response.put("status", "PENDING");
        //response.put("status", leave.getStatus().name());
        // TODO: 인증 기능 완성되면 사용자 이름 넣기
        // builder.name(dto.getUserName());

        return ResponseEntity.ok(response);
    }


    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        Optional<LeaveRequest> optionalLeave = leaveRequestRepository.findById(id);
        if (optionalLeave.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("신청 내역을 찾을 수 없습니다.");
        }

        LeaveRequest leave = optionalLeave.get();
        leave.setStatus(LeaveStatus.APPROVED); //승인 처리
        leaveRequestRepository.save(leave);

        return ResponseEntity.ok("승인 완료");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {
        Optional<LeaveRequest> optionalLeave = leaveRequestRepository.findById(id);
        if (optionalLeave.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("신청 내역을 찾을 수 없습니다.");
        }

        LeaveRequest leave = optionalLeave.get();
        leave.setStatus(LeaveStatus.REJECTED); // 반려 처리
        leaveRequestRepository.save(leave);

        return ResponseEntity.ok("반려 완료");
    }


    @GetMapping("/list")
    public ResponseEntity<List<LeaveRequest>> getAllRequests() {
        List<LeaveRequest> requests =
                    leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
        return ResponseEntity.ok(requests);
    }
}
