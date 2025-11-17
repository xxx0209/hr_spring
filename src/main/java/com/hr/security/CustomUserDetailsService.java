package com.hr.security;

import com.hr.entity.Member;
import com.hr.entity.Position;
import com.hr.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    //UserDetailsService는 Spring Security의 핵심 인터페이스 중 하나
    //인증 시(UsernamePasswordAuthenticationToken)
    //Spring Security가 자동으로 이 인터페이스의
    //loadUserByUsername() 메서드를 호출하여
    //사용자 정보를 조회
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        System.out.println(memberId);

        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        // Position 객체 초기화 (Lazy Loading)
        Position position = member.getPosition(); // Position은 lazy load이므로 여전히 null일 수 있음
        Hibernate.initialize(position); // 세션이 열린 상태에서 초기화

        // Role이 null일 수 있으므로 체크
        String role = member.getMemberRole() != null ? member.getMemberRole().name() : "ROLE_USER"; // 기본 ROLE_USER

        // CustomUserDetails 반환
        return CustomUserDetails.builder()
                .username(member.getId())
                .password(member.getPassword())
                .authorities(List.of(() -> role)) // Spring Security 권장: ROLE_ prefix
                .memberId(member.getId())
                .name(member.getName())
                .positionName(position != null ? position.getPositionName() : "직급 없음") // Position이 null일 경우 기본값 설정
                .email(member.getEmail())
                .build();
    }
}
