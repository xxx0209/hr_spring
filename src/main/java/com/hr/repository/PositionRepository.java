package com.hr.repository;


import com.hr.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    //직급 조회
    Optional<Position> findByPositionCode(String positionCode);
    //활성화상태 목록 조회
    Page<Position> findAllByActiveTrue(Pageable pageable);
}
