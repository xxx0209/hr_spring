package com.hr.repository;

import com.hr.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByApprover_Id(String memberId);
    List<Approval> findByRequest_Id(Long requestId);
}
