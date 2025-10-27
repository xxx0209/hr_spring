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

    public Request save(RequestDto dto) {
        return requestRepository.save(dto.toEntity());
    }

    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    public List<Request> findByMember(String memberId) {
        return null; // requestRepository.findByMember_Id(memberId);
    }

    public Request update(Long id, RequestDto dto) {
        Request existing = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기안을 찾을 수 없습니다."));

        existing.setRequestType(dto.getRequestType());
        existing.setContent(dto.getContent());
        existing.setStartDate(dto.getStartDate() != null ? dto.getStartDate().atStartOfDay() : null);
        existing.setEndDate(dto.getEndDate() != null ? dto.getEndDate().atStartOfDay() : null);
        existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());

        return requestRepository.save(existing);
    }

    public void delete(Long id) {
        requestRepository.deleteById(id);
    }

}
