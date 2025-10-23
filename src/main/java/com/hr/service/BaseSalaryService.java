package com.hr.service;

import com.hr.entity.BaseSalary;
import com.hr.entity.Member;
import com.hr.repository.BaseSalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseSalaryService {

    private final BaseSalaryRepository baseSalaryRepository;

    public BaseSalary getSalaryForMember(Member member) {
        return baseSalaryRepository.findByTypeAndReferenceId("MEMBER", member.getId())
                .or(() -> baseSalaryRepository.findByTypeAndReferenceId("POSITION", member.getPosition().getTitle()))
                .orElseThrow(() -> new IllegalStateException("기준 급여 정보가 없습니다: " + member.getId()));
    }
}

