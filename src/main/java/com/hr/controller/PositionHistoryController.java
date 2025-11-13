package com.hr.controller;

import com.hr.dto.member.PositionDto;
import com.hr.dto.member.PositionHistoryDto;
import com.hr.service.PositionHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> save(@Valid @RequestBody PositionHistoryDto positionHistoryDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) { // 유효성 검사에 문제가 있음.
            // 에러 메시지들을 Map이나 List로 추출
            Map<String, String> errors = new HashMap<>();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });

            // 에러 메시지를 담아서 400 Bad Request 반환
            return ResponseEntity.badRequest().body(errors);
        }

        // 직급내역 로직 수행
        try {
            positionHistoryService.save(positionHistoryDto);
            return ResponseEntity.ok("직급이 변경 되었습니다.");
        } catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "직급변경중 오류가 발생하였습니다."));
        }
    }

    @PostMapping("/change")
    public ResponseEntity<?> changePosition(@Valid @RequestBody PositionHistoryDto positionHistoryDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) { // 유효성 검사에 문제가 있음.
            // 에러 메시지들을 Map이나 List로 추출
            Map<String, String> errors = new HashMap<>();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });

            // 에러 메시지를 담아서 400 Bad Request 반환
            return ResponseEntity.badRequest().body(errors);
        }
        // 직급 로직 수행
        try {
            positionHistoryService.changeMemberPosition(positionHistoryDto.getMemberId(), positionHistoryDto.getNewPositionId(), positionHistoryDto.getChangeReason());
            return ResponseEntity.ok("직급 변경 및 이력 추가 완료");
        } catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "직급 변경중 오류가 발생하였습니다."));
        }
    }
}
