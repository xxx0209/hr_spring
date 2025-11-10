package com.hr.repository;

import com.hr.constant.SalaryStatus;
import com.hr.entity.Member;
import com.hr.entity.MemberSalary;
import com.hr.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // MemberSalary 참조
    List<Salary> findByMemberSalary(MemberSalary memberSalary);

    List<Salary> findByStatusAndMember_IdOrderByPayDateDesc(SalaryStatus status, String memberId);

    List<Salary> findByStatusAndSalaryMonthOrderByPayDateDesc(SalaryStatus status, YearMonth salaryMonth);

    List<Salary> findByStatusAndMember_IdAndSalaryMonthOrderByPayDateDesc(SalaryStatus status, String memberId, YearMonth salaryMonth);

    // ======================
    // Pageable 기반 (페이징 + 검색)
    // ======================
    Page<Salary> findAll(Pageable pageable);

    Page<Salary> findByMember_NameContainingIgnoreCase(String memberName, Pageable pageable);

    Page<Salary> findBySalaryMonth(YearMonth salaryMonth, Pageable pageable);

    Page<Salary> findByMember_NameContainingIgnoreCaseAndSalaryMonth(String memberName, YearMonth salaryMonth, Pageable pageable);

    Page<Salary> findByStatus(SalaryStatus status, Pageable pageable);

    Page<Salary> findByStatusAndMember_NameContainingIgnoreCase(SalaryStatus status, String memberName, Pageable pageable);

    Page<Salary> findByStatusAndMember_Id(SalaryStatus status, String memberId, Pageable pageable);

    Page<Salary> findByStatusAndSalaryMonth(SalaryStatus status, YearMonth salaryMonth, Pageable pageable);

    Page<Salary> findByStatusAndMember_IdAndSalaryMonth(SalaryStatus status, String memberId, YearMonth salaryMonth, Pageable pageable);
}
