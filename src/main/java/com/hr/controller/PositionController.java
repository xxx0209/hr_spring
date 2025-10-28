package com.hr.controller;

import com.hr.dto.MemberDto;
import com.hr.dto.PositionDto;
import com.hr.dto.SimpleMemberDto;
import com.hr.dto.SimplePositionDto;
import com.hr.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/position")
public class PositionController {

    private final PositionService positionService;

    @GetMapping("list")
    public Page<PositionDto> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return positionService.getPositions(page, size);
    }

    @GetMapping("/{id}")
    public PositionDto detail(@PathVariable Long id) {
        return positionService.getPosition(id);
    }

    @PutMapping("/{id}")
    public PositionDto update(@PathVariable Long id, @RequestBody PositionDto dto) {
        return positionService.updatePosition(id, dto);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody PositionDto positionDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()) { // 유효성 검사에 문제가 있음.
            // 에러 메시지들을 Map이나 List로 추출
            Map<String, String> errors = new HashMap<>();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });

            // 에러 메시지를 담아서 400 Bad Request 반환
            return ResponseEntity.badRequest().body(errors);
        }

        // 회원가입 로직 수행
        positionService.save(positionDto);

        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("all")
    public List<SimplePositionDto> getAllPositions() {
        return positionService.findAll();
    }
}
