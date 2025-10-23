package com.hr.repository;

import com.hr.entity.DeductionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 공제 항목 관리 페이지
public interface DeductionTypeRepository extends JpaRepository<DeductionType, String> {

    // 공제 유형 코드로 조회
    Optional<DeductionType> findByTypeCode(String typeCode);
}

