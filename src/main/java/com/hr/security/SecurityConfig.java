package com.hr.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) //기본설정을 사용하되 @CrossOrigin 애노테이션 -> CorsConfigurationSource Bean이 있다면 그 설정 사용 없으면 기본값 적용(모든 요청 허용 X, 제한적)
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()    // 모든 요청 인증 없이 허용
//                )
                .csrf(AbstractHttpConfigurer::disable)  //JWT는 stateless 인증이므로 CSRF 보호가 필요 없음 → 비활성화
                //JWT 기반 인증은 세션을 사용하지 않음 → Stateless 서버는 인증 상태를 저장하지 않고, 매 요청마다 JWT 검증
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**",
                                "/member/**", //회원가입/아이디중복 체크
                                "/auth/**",
                        "/api/**").permitAll() //jwt 토큰관련/로그인/로그아웃
                        .anyRequest().authenticated()) //그 외 모든 요청 → 인증 필요
                //Spring Security의 기본 UsernamePasswordAuthenticationFilter 전에 jwtFilter를 실행
                //즉, 요청이 들어오면 JWT 확인 후 SecurityContext에 인증 정보를 세팅하고 이후 필터 진행
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //AuthenticationManager → Spring Security에서 로그인 인증을 처리하는 핵심 객체
    //AuthenticationConfiguration을 통해 기본 AuthenticationManager를 가져와서 Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 방식 (가장 권장)
        return new BCryptPasswordEncoder();
    }

    @Bean // CorsConfigurationSource는 다른 도메인(origin)에서 자원 요청시 브라우저가 허용 여부를 검사해주는 보안 정책입니다.
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 리액트 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 메소드 목록
        // 클라이언트가 서버에 요청시 모든 요청 정보를 허용하겠습니다.
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 쿠키, 세션 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source ;
    }
}