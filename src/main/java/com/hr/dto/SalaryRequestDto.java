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
// 급여 생성 요청시 사용,
public class SalaryRequestDto {
    private String memberId;
    private LocalDate payDate;
    private BigDecimal overtimeHours; // 초과근무 시간
    private List<TaxDeductionDto> deductions; // 공제 항목 선택
}
