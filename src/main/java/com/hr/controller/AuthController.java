package com.hr.controller;

import com.hr.dto.LoginDto;
import com.hr.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager; //로그인 시 인증 처리
    private final JwtUtil jwtUtil; //JWT 생성, 검증, 추출

    @PostMapping("/login")
    public ResponseEntity<?>  login(@RequestBody LoginDto loginDto, HttpServletResponse response) {

        try {
            //Spring Security AuthenticationManager 사용
            //인증 실패 시 예외 발생 → 자동 401 반환 가능

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getMemberId(), loginDto.getPassword()));

            //로그인 성공 시 AccessToken + RefreshToken 생성
            String accessToken = jwtUtil.generateAccessToken(loginDto.getMemberId());
            String refreshToken = jwtUtil.generateRefreshToken(loginDto.getMemberId());


            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true); //HttpOnly → JS에서 접근 불가, XSS 공격 방어
            accessCookie.setPath("/"); //도메인 전체에서 사용 가능
            accessCookie.setMaxAge(15 * 60); //만료시간 설정: AccessToken 15분
            response.addCookie(accessCookie);

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true); //HttpOnly → JS에서 접근 불가, XSS 공격 방어
            refreshCookie.setPath("/"); //도메인 전체에서 사용 가능
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); //만료시간 설정: RefreshToken 7일
            response.addCookie(refreshCookie);

            return ResponseEntity.ok("로그인 성공");

        } catch (BadCredentialsException e) {
            // 아이디/비밀번호 불일치
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("아이디 또는 비밀번호가 올바르지 않습니다.");
        } catch (UsernameNotFoundException e) {
            // 사용자 없음
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("사용자를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생: " + e.getMessage());
        }

    }

    //AccessToken 재발급 API
    @PostMapping("/refresh")
    public String refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                               HttpServletResponse response) {

        //RefreshToken이 유효하면 username 추출 → 새로운 AccessToken 발급
        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            String username = jwtUtil.extractUsername(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);

            //새로운 AccessToken 쿠키에 저장
            Cookie accessCookie = new Cookie("accessToken", newAccessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(15 * 60);
            response.addCookie(accessCookie);

            return "AccessToken 재발급 완료";
        }
        return "RefreshToken이 유효하지 않습니다.";
    }

    //JWT 쿠키 삭제
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0); //MaxAge=0 → 쿠키 즉시 삭제
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); //MaxAge=0 → 쿠키 즉시 삭제
        response.addCookie(refreshCookie);

        return "로그아웃 성공";
    }

}
