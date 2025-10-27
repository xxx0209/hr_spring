// 📁 controller/RequestController.java
package com.hr.controller;

import com.hr.dto.RequestDto;
import com.hr.entity.Request;
import com.hr.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public Request createRequest(@RequestBody RequestDto dto) {
        return requestService.save(dto);
    }

    @GetMapping
    public List<Request> getAll() {
        return requestService.findAll();
    }

    @GetMapping("/member/{memberId}")
    public List<Request> getByMember(@PathVariable String memberId) {
        return null; //requestService.findByMember(memberId);
    }

    @PutMapping("/{id}")
    public Request updateRequest(@PathVariable Long id, @RequestBody RequestDto dto) {
        return requestService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id) {
        requestService.delete(id);
    }

}
