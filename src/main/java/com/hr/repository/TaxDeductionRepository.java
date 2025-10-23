package com.hr.repository;

import com.hr.entity.Salary;
import com.hr.entity.TaxDeduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxDeductionRepository extends JpaRepository<TaxDeduction, Long> {

    // 급여에 연결된 공제 항목 삭제
    void deleteAllBySalary(Salary salary);
}


