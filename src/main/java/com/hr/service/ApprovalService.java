// 📁 service/ApprovalService.java
package com.hr.service;

import com.hr.dto.ApprovalDto;
import com.hr.entity.Approval;
import com.hr.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;

    public Approval save(ApprovalDto dto) {
        return approvalRepository.save(dto.toEntity());
    }

    public List<Approval> findByRequest(Long requestId) {
        return approvalRepository.findByRequest_Id(requestId);
    }
}
