package com.hr.service;

import com.hr.constant.BaseSalaryType;
import com.hr.dto.BaseSalaryDto;
import com.hr.entity.BaseSalary;
import com.hr.entity.Member;
import com.hr.repository.BaseSalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseSalaryService {

    private final BaseSalaryRepository baseSalaryRepository;

    // 기준급 조회 (기존 기능)
//    public BaseSalary getSalaryForMember(com.hr.entity.Member member) {
//        return baseSalaryRepository.findByTypeAndReferenceId(BaseSalaryType.MEMBER, member.getId())
//                .or(() -> baseSalaryRepository.findByTypeAndReferenceId(BaseSalaryType.POSITION, member.getPosition().getPositionCode()))
//                .orElseThrow(() -> new IllegalStateException("기준 급여 정보가 없습니다: " + member.getId()));
//    }
    public BaseSalary getSalaryForMember(Member member) {
        return baseSalaryRepository.findByTypeAndReferenceId(BaseSalaryType.MEMBER, member.getId())
                .or(() -> {
                    if (member.getPosition() == null) {
                        throw new IllegalStateException("직급 정보가 없어 기준급을 조회할 수 없습니다: " + member.getId());
                    }
                    return baseSalaryRepository.findByTypeAndReferenceId(BaseSalaryType.POSITION, member.getPosition().getPositionCode());
                })
                .orElseThrow(() -> new IllegalStateException("기준 급여 정보가 없습니다 (사원ID 또는 직급 기준): " + member.getId()));
    }


    // 기준급 등록 (신규 추가)
    public BaseSalary registerBaseSalary(BaseSalaryDto dto) {
        boolean exists = baseSalaryRepository.findByTypeAndReferenceId(dto.getType(), dto.getReferenceId()).isPresent();
        if (exists) {
            throw new IllegalStateException("이미 등록된 기준급입니다: " + dto.getReferenceId());
        }

        BaseSalary salary = new BaseSalary();
        salary.setType(dto.getType());
        salary.setReferenceId(dto.getReferenceId());
        salary.setBaseSalary(dto.getBaseSalary());
        salary.setHourlyRate(dto.getHourlyRate());

        return baseSalaryRepository.save(salary);
    }

    public BaseSalary updateBaseSalary(Long id, BaseSalaryDto dto) {
        BaseSalary salary = baseSalaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 기준급이 존재하지 않습니다: " + id));

        salary.setBaseSalary(dto.getBaseSalary());
        salary.setHourlyRate(dto.getHourlyRate());
        return baseSalaryRepository.save(salary);
    }

    public void deleteBaseSalary(Long id) {
        if (!baseSalaryRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 기준급이 존재하지 않습니다: " + id);
        }
        baseSalaryRepository.deleteById(id);
    }

}
