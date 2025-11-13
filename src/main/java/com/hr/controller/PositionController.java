package com.hr.controller;

import com.hr.dto.member.PositionDto;
import com.hr.dto.SimplePositionDto;
import com.hr.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

        // 직급 로직 수행
        try {
            positionService.save(positionDto);
            return ResponseEntity.ok("직급이 등록 되었습니다.");
        } catch(DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이미 등록된 직급코드 입니다."));
        }
    }

    @GetMapping("all")
    public List<SimplePositionDto> getAllPositions() {
        return positionService.findAll();
    }
}
