package com.hr.repository;

import com.hr.constant.SalaryStatus;
import com.hr.entity.Member;
import com.hr.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {

    // 직원별 급여 이력 조회
    List<Salary> findByMember_Id(String memberId);
    List<Salary> findByMember_IdOrderByPayDateDesc(String memberId);

    boolean existsByMemberAndPayDate(Member member, LocalDate payDate);
    List<Salary> findByMember_IdAndPayDateBetween(String memberId, LocalDate start, LocalDate end);
    List<Salary> findByPayDateBetween(LocalDate start, LocalDate end);
    List<Salary> findByStatus(SalaryStatus status);
}

