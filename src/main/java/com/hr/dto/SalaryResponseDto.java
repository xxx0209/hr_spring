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
// 급요 생성 결과 응답 급여 상세 조회 하면에서 사용
public class SalaryResponseDto {

    private Integer salaryId;
    private String memberId;
    private String memberName;

    private LocalDate payDate;

    private BigDecimal customBaseSalary;   // 개인 기준 기본급 (null 가능)
    private BigDecimal hoursBaseSalary;    // 초과근무 수당
    private BigDecimal grossPay;           // 총지급액
    private BigDecimal totalDeduction;     // 총 공제액
    private BigDecimal netPay;             // 실지급액

    private String status;

    private List<TaxDeductionDetailDto> deductions;
}
