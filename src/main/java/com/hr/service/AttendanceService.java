package com.hr.service;

import com.hr.dto.AttendanceDto;
import com.hr.entity.AttendanceEntity;
import com.hr.repository.AttendanceRepository;
import com.hr.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void checkIn() {
        String memberId = SecurityUtil.getLoginMemberId();
        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByMemberIdAndDate(memberId, today)) {
            throw new IllegalStateException("이미 출근 처리되었습니다.");
        }

        AttendanceEntity attendance = AttendanceEntity.builder()
                .memberId(memberId)
                .date(today)
                .checkInTime(LocalTime.now())
                .status("출근 완료")
                .build();

        attendanceRepository.save(attendance);
    }

    public void checkOut() {
        String memberId = SecurityUtil.getLoginMemberId();
        LocalDate today = LocalDate.now();

        AttendanceEntity attendance = attendanceRepository.findByMemberIdAndDate(memberId, today)
                .orElseThrow(() -> new IllegalStateException("출근 기록이 없습니다."));

        if (attendance.getCheckOutTime() != null) {
            throw new IllegalStateException("이미 퇴근 처리되었습니다.");
        }

        attendance.setCheckOutTime(LocalTime.now());
        attendance.setStatus("퇴근 완료");

        attendanceRepository.save(attendance);
    }

    // 출퇴근 기록을 가져오는 메소드
    public List<AttendanceDto> getAttendanceList(int page, int size) {

        String memberId = SecurityUtil.getLoginMemberId();
        //LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(page, size);  // 페이지 번호와 페이지 크기 설정

        Page<AttendanceEntity> attendancePage = attendanceRepository.findByMemberId(memberId, pageable);

        // AttendanceEntity를 AttendanceDTO로 변환
        return attendancePage.getContent().stream()
                .map(entity -> new AttendanceDto(
                        entity.getDate(),
                        entity.getCheckInTime(),
                        entity.getCheckOutTime(),
                        entity.getStatus()
                ))
                .collect(Collectors.toList());
    }

    // 특정 회원의 오늘 출퇴근 상태를 가져오는 메소드
    public String getAttendanceState() {

        String memberId = SecurityUtil.getLoginMemberId();
        LocalDate today = LocalDate.now();  // 오늘 날짜를 가져옴

        // 특정 회원의 오늘 출퇴근 기록을 조회
        Optional<AttendanceEntity> attendance = attendanceRepository.findByMemberIdAndDate(memberId, today);

        // 출퇴근 기록이 있다면 상태를 반환
        return attendance.map(AttendanceEntity::getStatus)
                .orElse("출근 전"); // 없으면 '출근 전'으로 기본값 설정
    }
}
