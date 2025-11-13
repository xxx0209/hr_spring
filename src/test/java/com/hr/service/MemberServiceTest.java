package com.hr.service;

import com.hr.dto.member.MemberDto;
import com.hr.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
//@Transactional
@TestPropertySource(locations = "classpath:application.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Test
    void save() {
        MemberDto memberDto = new MemberDto();
        //memberDto.setId("ljy02090209");
        memberDto.setName("홍길동");
        memberDto.setPassword("KLaaaaaa!@#123");
        memberDto.setEmail("ljy02090209@naver.com");
        memberDto.setGender("남");
        memberDto.setAddress("서울시 금천구 테스트");

        memberService.save(memberDto);
    }
}