package com.hr.service;

import com.hr.dto.PositionSalaryDto;
import com.hr.entity.Member;
import com.hr.entity.Position;
import com.hr.entity.PositionSalary;
import com.hr.repository.MemberRepository;
import com.hr.repository.PositionRepository;
import com.hr.repository.PositionSalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionSalaryService {

    private final PositionSalaryRepository positionSalaryRepository;
    private final PositionRepository positionRepository;

    // 등록
    public void save(PositionSalaryDto dto) {

        Position position = positionRepository.findById(dto.getPositionId()).orElseThrow(() -> new IllegalArgumentException("직급이 존재하지 않습니다."));;

        PositionSalary positionSalary = new PositionSalary();
        positionSalary.setTitle(dto.getTitle());
        positionSalary.setPosition(position);
        positionSalary.setBaseSalary(dto.getBaseSalary());
        positionSalary.setHourlyRate(dto.getHourlyRate());
        positionSalary.setActive(dto.getActive());

        positionSalaryRepository.save(positionSalary);

    }

    public List<PositionSalaryDto> findAll() {

        return  positionSalaryRepository.findAll()
                .stream()
                .map(p -> {
                    PositionSalaryDto dto = new PositionSalaryDto();
                    dto.setId(p.getId());
                    dto.setPositionId(p.getPosition().getPositionId());
                    dto.setTitle(p.getTitle());
                    dto.setPositionName(p.getPosition().getPositionName());
                    dto.setBaseSalary(p.getBaseSalary());
                    dto.setHourlyRate(p.getHourlyRate());
                    dto.setActive(p.getActive());
                    // 필요한 다른 필드들도 여기에 설정
                    return dto;
                })
                .toList();
    }

    public void delete(Long id) {
        PositionSalary positionSalary = positionSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        positionSalary.setActive(false);
        positionSalaryRepository.save(positionSalary);
    }

    // 직급 급여 수정
    public void update(Long id, PositionSalaryDto positionSalaryDto) {
        PositionSalary positionSalary = positionSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Position position = positionRepository.findById(positionSalaryDto.getPositionId()).orElseThrow(() -> new IllegalArgumentException("직급이 존재하지 않습니다."));;

        positionSalary.setTitle(positionSalaryDto.getTitle());
        positionSalary.setPosition(position);
        positionSalary.setBaseSalary(positionSalaryDto.getBaseSalary());
        positionSalary.setHourlyRate(positionSalaryDto.getHourlyRate());
        positionSalary.setActive(positionSalaryDto.getActive());

        positionSalaryRepository.save(positionSalary);
    }

    // 회원의 직급 급여만 가져오기
    public List<PositionSalaryDto> getPositionSalariesForMember(Member member) {
        if (member.getPosition() == null) {
            return List.of(); // 직급 없으면 빈 리스트
        }
        List<PositionSalary> salaries = positionSalaryRepository
                .findByPosition_PositionIdAndActiveTrue(member.getPosition().getPositionId());

        return salaries.stream().map(ps -> {
            PositionSalaryDto dto = new PositionSalaryDto();
            dto.setId(ps.getId());
            dto.setTitle(ps.getTitle());
            dto.setPositionId(ps.getPosition().getPositionId());
            dto.setPositionName(ps.getPosition().getPositionName());
            dto.setBaseSalary(ps.getBaseSalary());
            dto.setHourlyRate(ps.getHourlyRate());
            dto.setActive(ps.getActive());
            return dto;
        }).toList();
    }
}
