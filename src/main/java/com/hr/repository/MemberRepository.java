package com.hr.repository;

import com.hr.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {
    Page<Member> findByIdContaining(String memberId, Pageable pageable);

    Page<Member> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.position p WHERE p.positionName LIKE %:keyword%")
    Page<Member> findByPositionNameContaining(@Param("keyword") String keyword, Pageable pageable);

    Page<Member> findByHiredate(String hiredate, Pageable pageable);
}
