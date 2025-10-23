package com.hr.config;

import com.hr.entity.DeductionType;
import com.hr.repository.DeductionTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DeductionTypeRepository deductionTypeRepository;

    @PostConstruct
    public void initDeductionTypes() {
        if (deductionTypeRepository.count() == 0) {
            deductionTypeRepository.save(new DeductionType("NP", "국민연금", new BigDecimal("0.045")));
            deductionTypeRepository.save(new DeductionType("HI", "건강보험", new BigDecimal("0.0354")));
            deductionTypeRepository.save(new DeductionType("EI", "고용보험", new BigDecimal("0.009")));
            deductionTypeRepository.save(new DeductionType("IT", "소득세", new BigDecimal("0.0003")));
        }
    }
}

