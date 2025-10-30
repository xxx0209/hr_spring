package com.hr.repository;

import com.hr.entity.Schedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // ✅ member, category를 함께 즉시 로딩
    @EntityGraph(attributePaths = {"member", "category"})
    List<Schedule> findByMemberId(String memberId);
}
