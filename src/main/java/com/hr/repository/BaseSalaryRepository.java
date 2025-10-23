package com.hr.repository;

import com.hr.constant.BaseSalaryType;
import com.hr.entity.BaseSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseSalaryRepository extends JpaRepository<BaseSalary, Long> {

    // 기준 급여 조회: type = "POSITION" 또는 "MEMBER"
    Optional<BaseSalary> findByTypeAndReferenceId(BaseSalaryType type, String referenceId);

}
