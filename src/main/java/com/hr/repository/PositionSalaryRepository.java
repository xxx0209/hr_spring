package com.hr.repository;

import com.hr.entity.PositionSalary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionSalaryRepository extends JpaRepository<PositionSalary, Long> {

    // 직급명 검색
    Page<PositionSalary> findByPosition_PositionNameContainingIgnoreCase(String positionName, Pageable pageable);

    Page<PositionSalary> findByPosition_PositionNameContainingIgnoreCaseAndActive(String positionName, Boolean active, Pageable pageable);

    Page<PositionSalary> findByActive(Boolean active, Pageable pageable);

    // 직급Id 기준 활성화된 PositionSalary 조회
    List<PositionSalary> findByPosition_PositionIdAndActiveTrue(Long positionId);
}
