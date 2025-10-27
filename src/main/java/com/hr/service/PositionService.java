package com.hr.service;

import com.hr.dto.*;
import com.hr.entity.Position;
import com.hr.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final PositionRepository positionRepository;

    public void save(PositionDto positionDto) {
        Position position = positionDto.toEntity();
        positionRepository.save(position);
    }

    public Page<PositionDto> getPositions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("positionId").descending());

        return positionRepository.findAll(pageable)
                .map(p -> PositionDto.fromEntity(p, PositionDto.class));
    }

    public PositionDto getPosition(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("직급 없음"));

        return PositionDto.fromEntity(position, PositionDto.class);
    }

    public PositionDto updatePosition(Long id, PositionDto dto) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("직급 없음"));

        position.setPositionName(dto.getPositionName());
        position.setDescription(dto.getDescription());
        position.setActive(dto.getActive());

        return PositionDto.fromEntity(position, PositionDto.class);
    }

    public List<SimplePositionDto> findAll() {
        return positionRepository.findAll()
                .stream()
                .map(p -> SimpleMemberDto.fromEntity(p, SimplePositionDto.class))
                .toList();
    }
}
