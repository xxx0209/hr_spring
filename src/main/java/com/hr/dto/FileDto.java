// 📁 dto/FileDto.java
package com.hr.dto;

import com.hr.entity.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto extends BaseDto<File> {

    private Long id;
    private Long requestId;
    private String fileName;
    private String filePath;
    private Long fileSize;

    @Override
    protected Class<File> getEntityClass() {
        return File.class;
    }

}
