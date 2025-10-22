// 📁 controller/FileController.java
package com.hr.controller;

import com.hr.dto.FileDto;
import com.hr.entity.File;
import com.hr.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public File uploadFile(@RequestBody FileDto dto) {
        return fileService.save(dto);
    }

    @GetMapping("/request/{requestId}")
    public List<File> getFilesByRequest(@PathVariable Long requestId) {
        return fileService.findByRequest(requestId);
    }
}
