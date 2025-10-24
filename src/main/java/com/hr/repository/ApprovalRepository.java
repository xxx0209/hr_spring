package com.hr.repository;

import com.hr.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    //List<Approval> findById(String memberId);
    //List<Approval> findByApprover_member_id(String memberId);
}
