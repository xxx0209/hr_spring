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
        return requestRepository.findByMember_Id(memberId);
    }
}
