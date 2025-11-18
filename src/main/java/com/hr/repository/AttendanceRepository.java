package com.hr.repository;

import com.hr.entity.AttendanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    boolean existsByMemberIdAndDate(String memberId, LocalDate date);
    Optional<AttendanceEntity> findByMemberIdAndDate(String memberId, LocalDate date);

    // memberId와 date에 해당하는 출퇴근 기록을 조회
    Page<AttendanceEntity> findByMemberId(String memberId, Pageable pageable);
}
