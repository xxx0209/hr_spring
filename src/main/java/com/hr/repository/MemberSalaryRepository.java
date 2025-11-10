package com.hr.repository;

import com.hr.entity.MemberSalary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberSalaryRepository extends JpaRepository<MemberSalary, Long> {

    Optional<MemberSalary> findByMember_Id(String memberId);

    List<MemberSalary> findAllByMember_Id(String memberId);

    // ======================
    // Pageable 기반
    // ======================
    Page<MemberSalary> findAll(Pageable pageable);

    Page<MemberSalary> findByMember_NameContainingIgnoreCase(String memberName, Pageable pageable);

    Page<MemberSalary> findByMember_Id(String memberId, Pageable pageable);
}
