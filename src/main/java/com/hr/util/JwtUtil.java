package com.hr.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

//JwtUtil 클래스는 **JWT(Json Web Token)**을 생성하고 검증하는 핵심 유틸리티
@Component
public class JwtUtil {

    // SECRET_KEY는 JWT 서명을 위해 사용되는 **비밀 키(secret key)**
    // 이 키는 서버만 알고 있어야 하며, 노출되면 누구나 토큰을 위조할 수 있음
    // 안전하게 설정하는 방법 최소 32바이트(길이 중요!)
//    private static final String SECRET_KEY = "your-very-strong-secret-key-should-be-long-32bytes-minimum";
    @Value("${jwt.secret}")
    private String secretKey;

    //JWT 토큰을 생성하는 메서드입니다.
    public String generateToken(String username, long expireMillis) {
        return Jwts.builder()
                .setSubject(username) //로그인 사용자 이름
                .setIssuedAt(new Date()) // 발급 시각
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis)) //만료 시각
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) //비밀키로 서명
                .compact(); //최종적으로 JWT 문자열 생성
    }

    //JWT 토큰에서 사용자 이름(username)을 꺼내는 메서드
    public String extractUsername(String token) {
        return Jwts.parserBuilder() //JWT 파서(해석기) 생성
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))//비밀키 지정 (토큰 위조 여부 확인)
                .build()
                .parseClaimsJws(token)//토큰을 해석하고, 서명 검증 수행
                .getBody()//JWT의 Payload(Claims) 부분 추출 (전송 과정에서 실제 목적이 되는 데이터 자체)
                .getSubject(); //username 반환
    }

    //토큰이 정상적이고 유효한지 검사합니다.
    //검증 항목
    //서명이 올바른지 (비밀키 일치)
    //토큰이 만료되지 않았는지 (exp 확인)
    //토큰 형식이 올바른지
    // ExpiredJwtException 토큰 만료
    // MalformedJwtException 형식 오류
    // SignatureException 서명 불일치(위조된 토큰)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() //
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
