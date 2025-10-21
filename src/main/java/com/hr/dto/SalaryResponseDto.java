package com.hr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
// 급요 생성 결과 응답
public class SalaryResponseDto {
    private Integer salaryId;
    private String memberId;
    private LocalDate payDate ;

    private BigDecimal baseSalary;
    private BigDecimal overtimePay;
    private BigDecimal grossPay;
    private BigDecimal totalDeduction;
    private BigDecimal netPay;


    private String status;
    private List<TaxDeductionDetailDto> deductions ;
}
