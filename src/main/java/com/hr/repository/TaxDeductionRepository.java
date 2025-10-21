package com.hr.repository;

import com.hr.entity.TaxDeduction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxDeductionRepository extends JpaRepository<TaxDeduction, Integer> {
    List<TaxDeduction> findBySalaryId(Integer salaryId);

}
