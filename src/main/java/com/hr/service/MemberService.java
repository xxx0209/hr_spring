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
public class MemberService {

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

    public Optional<MemberDto> findById(String memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        return Optional.ofNullable(MemberDto.of(member));

    }

}
