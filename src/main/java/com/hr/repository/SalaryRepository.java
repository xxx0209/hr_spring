package com.hr.repository;

import com.hr.constant.SalaryStatus;
import com.hr.entity.Member;
import com.hr.entity.MemberSalary;
import com.hr.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {

    // 급여 생성 시 중복 체크
    boolean existsByMemberAndSalaryMonth(Member member, YearMonth salaryMonth);

    // 상태별 급여 최신순 조회
    List<Salary> findByStatusOrderByPayDateDesc(SalaryStatus status);

    // 특정 회원의 급여 내역 (지급 완료 + 최신순)
    List<Salary> findByMember_IdAndStatusOrderByPayDateDesc(String memberId, SalaryStatus status);

    // 특정 급여 상세 조회
    Optional<Salary> findBySalaryIdAndMemberId(Integer salaryId, String memberId);
    List<Salary> findByMemberSalary(MemberSalary memberSalary);

    List<Salary> findByStatusAndMember_IdOrderByPayDateDesc(SalaryStatus status, String memberId);

    List<Salary> findByStatusAndSalaryMonthOrderByPayDateDesc(SalaryStatus status, YearMonth salaryMonth);

    List<Salary> findByStatusAndMember_IdAndSalaryMonthOrderByPayDateDesc(SalaryStatus status, String memberId, YearMonth salaryMonth);

}
