package com.hr.service;

import com.hr.constant.Role;
import com.hr.dto.MemberDto;
import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(MemberDto memberDto) {

        Member member = memberDto.careteMember();

        memberRepository.save(member);
    }

    public Boolean existsById(String memberId) {
        return memberRepository.existsById(memberId);
    }

    public Optional<MemberDto> findById(String name) {
        Member member = memberRepository.findById(name).orElse(null);
        return Optional.ofNullable(MemberDto.of(member));
    }
}
