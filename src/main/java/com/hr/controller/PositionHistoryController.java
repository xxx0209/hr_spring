package com.hr.controller;

import com.hr.dto.PositionHistoryDto;
import com.hr.service.PositionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/position/history")
@RequiredArgsConstructor
public class PositionHistoryController {

    private final PositionHistoryService positionHistoryService;

    @GetMapping("list")
    public ResponseEntity<List<PositionHistoryDto>> list() {
        return ResponseEntity.ok(positionHistoryService.findAll());
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
