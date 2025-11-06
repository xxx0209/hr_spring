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
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {

    // 급여 생성 시 중복 체크
    boolean existsByMemberAndSalaryMonth(Member member, YearMonth salaryMonth);

    // 직원 급여 이력
    List<Salary> findByMember(Member member);

    // 특정 회원의 특정 급여월 조회
    Optional<Salary> findByMemberAndSalaryMonth(Member member, YearMonth salaryMonth);

    // 상태별 급여 조회
    List<Salary> findByStatus(SalaryStatus status);
    List<Salary> findByMemberIdAndStatus(String memberId, String status);

    Optional<Salary> findBySalaryIdAndMemberId(Integer salaryId, String memberId);

    // 조건별 조회
    List<Salary> findByMember_IdAndSalaryMonthAndStatus(String memberId, YearMonth salaryMonth, SalaryStatus status);

    List<Salary> findByMember_IdAndStatusOrderByPayDateDesc(String memberId, SalaryStatus status);

    List<Salary> findBySalaryMonthAndStatus(YearMonth salaryMonth, SalaryStatus status);

    // 페이징용
    Page<Salary> findByMember_IdAndSalaryMonthAndStatus(String memberId, YearMonth salaryMonth, SalaryStatus status, Pageable pageable);
    Page<Salary> findBySalaryMonth(YearMonth salaryMonth, Pageable pageable);


}




