package com.hr.repository;

import com.hr.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByRequest_Id(Long requestId);
}
