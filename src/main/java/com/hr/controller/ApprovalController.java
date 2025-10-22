// 📁 controller/ApprovalController.java
package com.hr.controller;

import com.hr.dto.ApprovalDto;
import com.hr.entity.Approval;
import com.hr.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping
    public Approval create(@RequestBody ApprovalDto dto) {
        return approvalService.save(dto);
    }

    @GetMapping("/request/{requestId}")
    public List<Approval> getByRequest(@PathVariable Long requestId) {
        return approvalService.findByRequest(requestId);
    }
}
