package com.hr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
// 공제 항목 관리
public class TaxDeductionDto {
    private String typeCode; // 공제 유형 코드
    private BigDecimal rate; // nullable, defaultRate 사용 가능
}
