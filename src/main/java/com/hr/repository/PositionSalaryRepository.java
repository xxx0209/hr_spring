package com.hr.repository;

import com.hr.entity.PositionSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionSalaryRepository extends JpaRepository<PositionSalary, Long> {
    Optional<PositionSalary> findByPosition_PositionName(String positionName);

    // 직급Id 기준 활성화된 PositionSalary 조회
    List<PositionSalary> findByPosition_PositionIdAndActiveTrue(Long positionId);
}

