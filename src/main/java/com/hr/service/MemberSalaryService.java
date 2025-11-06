package com.hr.service;

import com.hr.dto.MemberSalaryDto;
import com.hr.entity.Member;
import com.hr.entity.MemberSalary;
import com.hr.entity.Position;
import com.hr.entity.PositionSalary;
import com.hr.repository.MemberRepository;
import com.hr.repository.MemberSalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSalaryService {

   private final MemberSalaryRepository memberSalaryRepository;
   private final MemberRepository memberRepository;

   // 맴버 기준급 등록
    public void save(MemberSalaryDto dto){


       Member member =memberRepository.findById(dto.getMemberId()).orElseThrow(()->new IllegalArgumentException("회원이 존재하지 않습니다"));

        MemberSalary memberSalary = new MemberSalary();
        memberSalary.setMember(member);
        memberSalary.setBaseSalary(dto.getBaseSalary());
        memberSalary.setHourlyRate(dto.getHourlyRate());

        memberSalaryRepository.save(memberSalary);

    }

    // 맴버 기준급 전체 조회
    public List<MemberSalaryDto> findAll() {

        return  memberSalaryRepository.findAll()
                .stream()
                .map(m->{
                    MemberSalaryDto dto = new MemberSalaryDto();
                    dto.setId(m.getId());
                    dto.setMemberId(m.getMember().getId());
                    dto.setMemberName(m.getMember().getName());
                    dto.setBaseSalary(m.getBaseSalary());
                    dto.setHourlyRate(m.getHourlyRate());
                    return dto;
                })
                .toList();
    }

    // 맴버 기준급 삭제
    public void delete(Long id) {
        MemberSalary memberSalary = memberSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("member not found"));
        memberSalaryRepository.delete(memberSalary);
    }

    // 맴버 급여 수정
    public void update(Long id, MemberSalaryDto dto) {
       MemberSalary memberSalary = memberSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("memberSalary not found"));

        memberSalary.setBaseSalary(dto.getBaseSalary());
        memberSalary.setHourlyRate(dto.getHourlyRate());

        memberSalaryRepository.save(memberSalary);
    }
}
