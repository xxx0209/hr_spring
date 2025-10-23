package com.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxDeductionDto {

    private String typeCode;         // 공제 유형 코드 (예: NP, HI, EI, IT)
    private BigDecimal rate;         // 공제율 (null이면 기본값 사용)
}
