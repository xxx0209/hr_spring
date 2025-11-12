package com.hr.service;

import com.hr.dto.PositionSalaryDto;
import com.hr.entity.Member;
import com.hr.entity.Position;
import com.hr.entity.PositionSalary;
import com.hr.repository.PositionRepository;
import com.hr.repository.PositionSalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionSalaryService {

    private final PositionSalaryRepository positionSalaryRepository;
    private final PositionRepository positionRepository;

    // 등록
    public void save(PositionSalaryDto dto) {
        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("직급이 존재하지 않습니다."));

        PositionSalary positionSalary = new PositionSalary();
        positionSalary.setTitle(dto.getTitle());
        positionSalary.setPosition(position);
        positionSalary.setBaseSalary(dto.getBaseSalary());
        positionSalary.setHourlyRate(dto.getHourlyRate());
        positionSalary.setActive(dto.getActive());

        positionSalaryRepository.save(positionSalary);
    }

    // 전체 조회 (페이징 + 검색 + 활성/비활성)
    public Page<PositionSalaryDto> findAll(Pageable pageable, String positionName, Boolean active) {
        Page<PositionSalary> page;

        if (positionName != null && !positionName.isBlank() && active != null) {
            page = positionSalaryRepository.findByPosition_PositionNameContainingIgnoreCaseAndActive(positionName, active, pageable);
        } else if (positionName != null && !positionName.isBlank()) {
            page = positionSalaryRepository.findByPosition_PositionNameContainingIgnoreCase(positionName, pageable);
        } else if (active != null) {
            page = positionSalaryRepository.findByActive(active, pageable);
        } else {
            page = positionSalaryRepository.findAll(pageable);
        }

        return page.map(ps -> {
            PositionSalaryDto dto = new PositionSalaryDto();
            dto.setId(ps.getId());
            dto.setTitle(ps.getTitle());
            dto.setPositionId(ps.getPosition().getPositionId());
            dto.setPositionName(ps.getPosition().getPositionName());
            dto.setBaseSalary(ps.getBaseSalary());
            dto.setHourlyRate(ps.getHourlyRate());
            dto.setActive(ps.getActive());
            return dto;
        });
    }

    // 수정
    public void update(Long id, PositionSalaryDto dto) {
        PositionSalary positionSalary = positionSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PositionSalary not found"));

        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("직급이 존재하지 않습니다."));

        positionSalary.setTitle(dto.getTitle());
        positionSalary.setPosition(position);
        positionSalary.setBaseSalary(dto.getBaseSalary());
        positionSalary.setHourlyRate(dto.getHourlyRate());
        positionSalary.setActive(dto.getActive());

        positionSalaryRepository.save(positionSalary);
    }

    // 삭제 (비활성 처리)
    public void delete(Long id) {
        PositionSalary positionSalary = positionSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PositionSalary not found"));
        positionSalary.setActive(false);
        positionSalaryRepository.save(positionSalary);
    }

    // 회원 기준 직급급여 조회 (페이징 없이 전체)
    public List<PositionSalaryDto> getPositionSalariesForMember(Member member) {
        if (member.getPosition() == null) return List.of();

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
        }).collect(Collectors.toList());
    }
}
