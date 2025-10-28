package com.hr.service;

import com.hr.dto.PositionHistoryDto;
import com.hr.entity.Member;
import com.hr.entity.Position;
import com.hr.entity.PositionHistory;
import com.hr.repository.MemberRepository;
import com.hr.repository.PositionHistoryRepository;
import com.hr.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionHistoryService {

    private final PositionHistoryRepository positionHistoryRepository;
    private final MemberRepository memberRepository;
    private final PositionRepository positionRepository;

    public Page<PositionHistoryDto> findAll(Pageable pageable) {
        return positionHistoryRepository.findAllAsDto(pageable);
    }

    public PositionHistoryDto save(PositionHistoryDto positionHistoryDto) {
        Member member = memberRepository.findById(positionHistoryDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Position oldPos = (positionHistoryDto.getOldPositionId() != null)
                ? positionRepository.findById(positionHistoryDto.getOldPositionId()).orElse(null)
                : null;

        Position newPos = positionRepository.findById(positionHistoryDto.getNewPositionId())
                .orElseThrow(() -> new RuntimeException("New Position not found"));

        PositionHistory ph = positionHistoryDto.toEntity();
        ph.setOldPosition(oldPos);
        ph.setNewPosition(newPos);

        positionHistoryRepository.save(ph);

        return PositionHistoryDto.fromEntity(ph, PositionHistoryDto.class);
    }

    //여기서 부터 다시
    public void changeMemberPosition(String memberId, Long newPositionId, String reason) {

        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 기존 직급, 새 직급 조회
        Position oldPosition = member.getPosition();
        Position newPosition = positionRepository.findById(newPositionId)
                .orElseThrow(() -> new IllegalArgumentException("직급이 존재하지 않습니다."));

        // 회원 직급 변경
        member.setPosition(newPosition);


        // 이력 저장
        PositionHistory history = PositionHistory.builder()
                .member(member)
                .oldPosition(oldPosition)
                .newPosition(newPosition)
                .changeReason(reason)
                .changedAt(LocalDateTime.now())
                .build();

        positionHistoryRepository.save(history);
    }
}

