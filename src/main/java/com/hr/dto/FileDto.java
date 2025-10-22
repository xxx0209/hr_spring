// 📁 dto/FileDto.java
package com.hr.dto;

import com.hr.entity.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto extends BaseDto {

    private Long id;
    private Long requestId;
    private String fileName;
    private String filePath;
    private Long fileSize;

    public File toEntity() {
        return modelMapper.map(this, File.class);
    }

    public static FileDto of(File entity) {
        return modelMapper.map(entity, FileDto.class);
    }
}
