package com.hr.repository;

import com.hr.entity.MemberSalary;
import com.hr.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberSalaryRepository extends JpaRepository<MemberSalary, Long> {
    Optional<MemberSalary> findByMember_Id(String memberId);
    List<MemberSalary> findAllByMember_Id(String memberId);


}

