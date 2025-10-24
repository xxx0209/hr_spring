package com.hr.repository;

import com.hr.constant.SalaryStatus;
import com.hr.entity.Member;
import com.hr.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {

    // 🔹 급여 생성 시 중복 체크
    boolean existsByMemberAndSalaryMonth(Member member, YearMonth salaryMonth);
    boolean existsByMemberAndPayDate(Member member, LocalDate payDate);

    // 🔹 직원 급여 이력 (비페이징)
    List<Salary> findByMember_IdOrderByPayDateDesc(String memberId);
    // 🔹 상태별 급여 조회 (비페이징)
    List<Salary> findByStatus(SalaryStatus status);
    List<Salary> findByPayDateBetween(LocalDate start, LocalDate end);

    List<Salary> findByMember_IdAndPayDateBetween(String memberId, LocalDate start, LocalDate end);
    List<Salary> findByMember_IdAndPayDateBetweenAndStatus(String memberId, LocalDate start, LocalDate end, SalaryStatus status);


    // 🔹 월별 급여 조회 - 직원 (페이징)
    Page<Salary> findByMember_IdAndSalaryMonthAndStatus(String memberId, YearMonth salaryMonth, SalaryStatus status, Pageable pageable);
    // 🔹 월별 급여 조회 - 관리자 (페이징)
    Page<Salary> findBySalaryMonth(YearMonth salaryMonth, Pageable pageable);

}


