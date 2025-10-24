package com.hr.repository;

import com.hr.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    default List<Request> findByMember_Id(String memberId){
        return null;
    }
}
