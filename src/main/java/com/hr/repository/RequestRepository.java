package com.hr.repository;

import com.hr.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByMemberId(String memberId);

    List<Request> findByMemberIdAndRequestTypeAndStatusAndStartDateBetween(
            String memberId,
            String requestType,
            String status,
            LocalDateTime start,
            LocalDateTime end
    );


}
