package com.hr.config;

import com.hr.constant.BaseSalaryType;
import com.hr.entity.BaseSalary;
import com.hr.entity.DeductionType;
import com.hr.repository.BaseSalaryRepository;
import com.hr.repository.DeductionTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DeductionTypeRepository deductionTypeRepository;
    private final BaseSalaryRepository baseSalaryRepository;

    @PostConstruct
    public void initData() {
        initDeductionTypes();
        initPositionBaseSalaries();
    }

    private void initPositionBaseSalaries() {
        if(baseSalaryRepository.count() == 0){
            baseSalaryRepository.save(new BaseSalary(
                    BaseSalaryType.POSITION, "인턴", new BigDecimal("2300000"), new BigDecimal("10000")));
            baseSalaryRepository.save(new BaseSalary(
                    BaseSalaryType.POSITION, "직원", new BigDecimal("2600000"), new BigDecimal("12000")));
            baseSalaryRepository.save(new BaseSalary(
                    BaseSalaryType.POSITION, "매니저", new BigDecimal("4000000"), new BigDecimal("14000")));
            baseSalaryRepository.save(new BaseSalary(
                    BaseSalaryType.POSITION, "사장", new BigDecimal("6000000"), new BigDecimal("20000")));
        }
    }

    public void initDeductionTypes() {
        if (deductionTypeRepository.count() == 0) {
            deductionTypeRepository.save(new DeductionType("NP", "국민연금", new BigDecimal("0.045")));
            deductionTypeRepository.save(new DeductionType("HI", "건강보험", new BigDecimal("0.0354")));
            deductionTypeRepository.save(new DeductionType("EI", "고용보험", new BigDecimal("0.009")));
            deductionTypeRepository.save(new DeductionType("IT", "소득세", new BigDecimal("0.0003")));
        }
    }
}

