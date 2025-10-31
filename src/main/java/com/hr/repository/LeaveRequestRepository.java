package com.hr.repository;

import com.hr.entity.LeaveRequest;
import com.hr.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    //기본 CRUD 메서드 포함됨.
        List<LeaveRequest> findByStatus(LeaveStatus status);
}
