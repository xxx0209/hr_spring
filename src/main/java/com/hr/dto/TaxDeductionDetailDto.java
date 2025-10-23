package com.hr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class TaxDeductionDetailDto {

    private String typeCode;
    private String typeName;
    private BigDecimal rate;
    private BigDecimal amount;
}
