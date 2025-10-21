package com.hr.repository;

import com.hr.entity.DeductionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeductionTypeRepository extends JpaRepository<DeductionType, String> {
}
