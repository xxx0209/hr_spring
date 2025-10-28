package com.hr.service;

import com.hr.dto.BaseDto;
import com.hr.dto.MemberDto;
import com.hr.dto.PositionHistoryDto;
import com.hr.dto.SimpleMemberDto;
import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(MemberDto memberDto) {

        //패스워드 암호화
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        Member member = memberDto.toEntity();

        memberRepository.save(member);
    }

    public Boolean existsById(String memberId) {
        return memberRepository.existsById(memberId);
    }

    public Optional<MemberDto> findById(String memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        return Optional.ofNullable(BaseDto.fromEntity(member, MemberDto.class));

    }

    public List<SimpleMemberDto> findAll() {

        return memberRepository.findAll()
                .stream()
                //.peek(m -> Hibernate.initialize(m.getPosition()))
                .map(p -> SimpleMemberDto.fromEntity(p, SimpleMemberDto.class))
                .collect(Collectors.toList());
    }
}
