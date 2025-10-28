package com.hr.controller;

import com.hr.dto.PositionHistoryDto;
import com.hr.service.PositionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/position/history")
@RequiredArgsConstructor
public class PositionHistoryController {

    private final PositionHistoryService positionHistoryService;

    @GetMapping("list")
    public Page<PositionHistoryDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());
        return positionHistoryService.findAll(pageable);
    }

    @PostMapping("save")
    public ResponseEntity<PositionHistoryDto> save(@RequestBody PositionHistoryDto dto) {
        return ResponseEntity.ok(positionHistoryService.save(dto));
    }

    @PostMapping("/change")
    public ResponseEntity<String> changePosition(@RequestParam String memberId,
                                                 @RequestParam Long newPositionId,
                                                 @RequestParam(required = false) String reason) {
        positionHistoryService.changeMemberPosition(memberId, newPositionId, reason);
        return ResponseEntity.ok("직급 변경 및 이력 추가 완료");
    }
}
