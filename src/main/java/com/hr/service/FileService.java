// 📁 service/FileService.java
package com.hr.service;

import com.hr.dto.FileDto;
import com.hr.entity.File;
import com.hr.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public File save(FileDto dto) {
        return fileRepository.save(dto.toEntity());
    }

    public List<File> findByRequest(Long requestId) {
        return fileRepository.findByRequest_Id(requestId);
    }
}
