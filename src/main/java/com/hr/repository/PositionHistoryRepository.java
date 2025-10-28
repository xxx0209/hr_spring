package com.hr.repository;

import com.hr.dto.PositionHistoryDto;
import com.hr.entity.PositionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PositionHistoryRepository extends JpaRepository<PositionHistory, Long> {

    @Query(
            value = """
            SELECT new com.hr.dto.PositionHistoryDto(
                h.id,
                m.id,
                m.name,
                oldP.positionId,
                oldP.positionName,
                newP.positionId,
                newP.positionName,
                h.changeReason,
                h.changedAt
            )
            FROM PositionHistory h
            JOIN h.member m
            LEFT JOIN h.oldPosition oldP
            JOIN h.newPosition newP
            ORDER BY h.changedAt DESC
        """,
            countQuery = "SELECT COUNT(h) FROM PositionHistory h"
    )
    Page<PositionHistoryDto> findAllAsDto(Pageable pageable);

}
