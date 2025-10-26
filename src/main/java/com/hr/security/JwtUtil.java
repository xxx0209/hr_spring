package com.hr.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

//JwtUtil 클래스는 **JWT(Json Web Token)**을 생성하고 검증하는 핵심 유틸리티

//페이로드(Payload)
//페이로드는 Claims(클레임) 라고 불리는 데이터 집합으로 이루어져 있어요.
//등록된 클레임(Registered Claims): JWT 표준에서 정의한 예약 키
//iss → 발급자(Issuer)
//sub → 주체(Subject)
//aud → 대상자(Audience)
//exp → 만료시간(Expiration Time)
//iat → 발급시간(Issued At)
//공개 클레임(Public Claims): 개발자가 자유롭게 정의 가능
//비공개 클레임(Private Claims): 시스템 내부에서만 사용
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey; //JWT 서명용 비밀키

    private Key key; //HMAC-SHA 키로 변환 후 사용.

    private final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 15;  // 엑세스 토큰 만료 시간 15분
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 리프레시 토큰 만료 시간 7일

    //HMAC-SHA256용 키 객체를 생성
    //매번 토큰 생성/검증 시 secretKey.getBytes()를 직접 쓰지 않고, 한 번 만들어 재사용
    private void init() {
        if (key == null) key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //토큰 생성 메서드(AccessToken)
    public String generateAccessToken(String username) {
        init();
        return createToken(username, ACCESS_TOKEN_EXPIRATION);
    }

    //토큰 생성 메서드(RefreshToken)
    public String generateRefreshToken(String username) {
        init();
        return createToken(username, REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(String username, long expireMillis) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username) // JWT 페이로드의 sub(주체) 필드에 username 저장
                .setIssuedAt(now)// 발행 시간
                .setExpiration(new Date(now.getTime() + expireMillis))// 만료 시간
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)// 비밀키로 HMAC-SHA256 서명
                .compact();  // 문자열로 변환
    }

    //JWT를 파싱해서 subject를 가져옴
    //예외 발생 시 JWT가 잘못된 것이므로 보통 호출하는 쪽에서 예외 처리 필요
    public String extractUsername(String token) {
        init();
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //토큰을 파싱해 문제가 없으면 true, 예외 발생 시 false
    //검증 항목: 서명 유효성, 만료 시간, 토큰 구조
    public boolean validateToken(String token) {
        init();
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
