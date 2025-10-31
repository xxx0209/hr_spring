package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@ToString
// 급여 생성 요청시 사용,
public class SalaryRequestDto {
    private String memberId;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth salaryMonth; // 급여 대상 월만 받음

//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate payDate;
    private BigDecimal overtimeHours; // 초과근무 시간
    private List<TaxDeductionDto> deductions; // 공제 항목 선택
}
