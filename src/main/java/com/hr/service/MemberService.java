package com.hr.service;

import com.hr.dto.MemberDto;
import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(MemberDto memberDto) {

        //패스워드 암호화
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        Member member = memberDto.careteMember();

        memberRepository.save(member);
    }

    public Boolean existsById(String memberId) {
        return memberRepository.existsById(memberId);
    }

    //UserDetailsService는 Spring Security의 핵심 인터페이스 중 하나
    //인증 시(UsernamePasswordAuthenticationToken)
    //Spring Security가 자동으로 이 인터페이스의
    //loadUserByUsername() 메서드를 호출하여
    //사용자 정보를 조회
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        //회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 앟는 사용자입니다."));
        //System.out.println(user.getAuthorities());
        // 출력: [ROLE_USER]
        return User.builder()
                .username(member.getId())
                .password(member.getPassword())
                .authorities(List.of(() -> "ROLE_USER")) //유저에게 부여할 권한(Role)을 지정합니다. Spring Security는 권한 이름 앞에 "ROLE_" 접두사를 권장
                .build();
    }

    public Optional<MemberDto> findById(String name) {
        Member member = memberRepository.findById(name).orElse(null);
        return Optional.ofNullable(MemberDto.of(member));

    }
}
