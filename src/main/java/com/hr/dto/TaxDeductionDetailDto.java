package com.hr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
// 급여 내 공제 항목 응답시 사용
public class TaxDeductionDetailDto {
    private String typeCode;
    private String typeName;
    private BigDecimal rate ;
    private BigDecimal amount;
}
