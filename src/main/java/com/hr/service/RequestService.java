// 📁 service/RequestService.java
package com.hr.service;

import com.hr.dto.RequestDto;
import com.hr.entity.Request;
import com.hr.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    // 저장 (등록)
    public Request save(RequestDto dto) {
        return requestRepository.save(dto.toEntity());
    }

    // 전체 조회
    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    // 특정 작성자별 조회 (현재 미사용)
    public List<Request> findByMember(String memberId) {
        return requestRepository.findAll()
                .stream()
                .filter(r -> memberId.equals(r.getMemberId()))
                .toList();
    }

    // 수정
    public Request update(Long id, RequestDto dto) {
        Request existing = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기안을 찾을 수 없습니다."));

        existing.setRequestType(dto.getRequestType());
        existing.setContent(dto.getContent());

        // LocalDate → LocalDateTime 변환 명확히
        if (dto.getStartDate() != null)
            existing.setStartDate(dto.getStartDate().atStartOfDay());
        else
            existing.setStartDate(null);

        if (dto.getEndDate() != null)
            existing.setEndDate(dto.getEndDate().atStartOfDay());
        else
            existing.setEndDate(null);

        existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        existing.setPrice(dto.getPrice() != null ? dto.getPrice() : existing.getPrice());

        return requestRepository.save(existing);
    }

    // 삭제
    public void delete(Long id) {
        requestRepository.deleteById(id);
    }

    // 상태 변경 (결재 요청 등)
    public void updateStatus(Long id, String status) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));
        request.setStatus(status);
        requestRepository.save(request);
    }
}
