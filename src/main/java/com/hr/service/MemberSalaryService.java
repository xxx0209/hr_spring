package com.hr.service;

import com.hr.dto.MemberSalaryDto;
import com.hr.entity.Member;
import com.hr.entity.MemberSalary;
import com.hr.entity.Salary;
import com.hr.repository.MemberRepository;
import com.hr.repository.MemberSalaryRepository;
import com.hr.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberSalaryService {

    private final MemberSalaryRepository memberSalaryRepository;
    private final MemberRepository memberRepository;
    private final SalaryRepository salaryRepository;

    // 등록
    public void save(MemberSalaryDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        MemberSalary memberSalary = new MemberSalary();
        memberSalary.setMember(member);
        memberSalary.setBaseSalary(dto.getBaseSalary());
        memberSalary.setHourlyRate(dto.getHourlyRate());

        memberSalaryRepository.save(memberSalary);
    }

    // 전체 조회 (페이징 + 검색)
    public Page<MemberSalaryDto> findAll(Pageable pageable, String memberId) {
        Page<MemberSalary> page;

        if (memberId == null || memberId.isEmpty()) {
            page = memberSalaryRepository.findAll(pageable);
        } else {
            // memberId 기준 검색
            page = memberSalaryRepository.findByMember_Id(memberId, pageable);
        }

        return page.map(m -> {
            MemberSalaryDto dto = new MemberSalaryDto();
            dto.setId(m.getId());
            dto.setMemberId(m.getMember().getId());
            dto.setMemberName(m.getMember().getName());
            dto.setBaseSalary(m.getBaseSalary());
            dto.setHourlyRate(m.getHourlyRate());
            return dto;
        });
    }

    // 삭제
    public void delete(Long id) {
        MemberSalary memberSalary = memberSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MemberSalary not found"));

        // MemberSalary 참조하는 Salary 해제
        List<Salary> salaries = salaryRepository.findByMemberSalary(memberSalary);
        for (Salary salary : salaries) {
            salary.setMemberSalary(null);
        }

        memberSalaryRepository.delete(memberSalary);
    }

    // 수정
    public void update(Long id, MemberSalaryDto dto) {
        MemberSalary memberSalary = memberSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MemberSalary not found"));

        memberSalary.setBaseSalary(dto.getBaseSalary());
        memberSalary.setHourlyRate(dto.getHourlyRate());

        memberSalaryRepository.save(memberSalary);
    }
}
