package com.hr.repository;

import com.hr.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    List<Salary> findByMemberId(String memberId); // 직원별 급여 내역 조회 메서드
    List<Salary> findByStatus(String status); // 지급 상태
    List<Salary> findByMemberIdAndPayDateBetween(String memberId, LocalDate start, LocalDate end);// 기가변 지급 내용

}
