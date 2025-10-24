package com.hr.controller;

import com.hr.dto.MemberDto;
import com.hr.entity.Member;
import com.hr.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody MemberDto memberDto, BindingResult bindingResult){

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
        memberService.save(memberDto);

        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/checkId")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestBody Map<String, String> request) {
        String memberId = request.get("id");
        if (null != request.get("memberId")) {
            memberId = request.get("memberId");
        }
        boolean available = !memberService.existsById(memberId);  // 존재하지 않으면 사용 가능
        return ResponseEntity.ok(Map.of("available", available));
    }

    @PostMapping("/login")
    public void login() {
        //로그인 처리구현 하기
        log.debug("로그인 처리 구현해야 함");
    }
}
