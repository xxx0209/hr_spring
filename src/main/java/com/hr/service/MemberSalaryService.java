package com.hr.service;

import com.hr.dto.MemberSalaryDto;
import com.hr.entity.*;
import com.hr.repository.MemberRepository;
import com.hr.repository.MemberSalaryRepository;
import com.hr.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSalaryService {

   private final MemberSalaryRepository memberSalaryRepository;
   private final MemberRepository memberRepository;
   private final SalaryRepository salaryRepository;

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

        // MemberSalary를 참조하는 Salary 조회
        List<Salary> salaries = salaryRepository.findByMemberSalary(memberSalary);

        // Salary에서 참조 해제
        for (Salary salary : salaries) {
            salary.setMemberSalary(null);
        }

        // MemberSalary 삭제
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
