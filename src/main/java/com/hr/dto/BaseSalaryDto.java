package com.hr.dto;

import com.hr.constant.BaseSalaryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class BaseSalaryDto {

    private BaseSalaryType type;             // "POSITION" 또는 "MEMBER"
    private String referenceId;      // 직급명 또는 memberId
    private BigDecimal baseSalary;
    private BigDecimal hourlyRate;
}
