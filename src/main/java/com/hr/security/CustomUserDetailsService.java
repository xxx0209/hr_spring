package com.hr.security;

import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        System.out.println(memberId);
        //회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 앟는 사용자입니다."));

        return CustomUserDetails.builder()
                .username(member.getId())
                .password(member.getPassword())
                .authorities(List.of(() -> member.getMemberRole().name()

                )) //유저에게 부여할 권한(Role)을 지정합니다. Spring Security는 권한 이름 앞에 "ROLE_" 접두사를 권장
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .build();
//        return User.builder()
//                .username(member.getId())
//                .password(member.getPassword())
//                .authorities(List.of(() -> member.getMemberRole().name()
//                )) //유저에게 부여할 권한(Role)을 지정합니다. Spring Security는 권한 이름 앞에 "ROLE_" 접두사를 권장
//                .build();
    }
}
