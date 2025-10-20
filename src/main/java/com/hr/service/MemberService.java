package com.hr.service;

import com.hr.constant.Role;
import com.hr.dto.MemberDto;
import com.hr.entity.Member;
import com.hr.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(MemberDto memberDto) {

        Member member = memberDto.careteMember();

        memberRepository.save(member);
    }
}
