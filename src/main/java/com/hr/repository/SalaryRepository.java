package com.hr.repository;

import com.hr.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {

    // 직원별 급여 이력 조회
    List<Salary> findByMember_Id(String memberId);

}
