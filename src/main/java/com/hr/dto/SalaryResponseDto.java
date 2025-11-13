package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr.constant.SalaryStatus;
import com.hr.constant.SalaryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class SalaryResponseDto {

    private Integer salaryId;

    private String memberId;            // Member 엔티티의 ID (String 타입으로 유지)
    private String memberName;          // 멤버 이름
    private Long positionId;        // 직급명 (POSITION 기준일 경우)
    private Long positionSalaryId;

    @JsonFormat(pattern = "yyyy-MM")
    private String salaryMonth;      // 급여 기준 월

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate payDate;          // 실제 지급일

    private SalaryType salaryType;      // 급여 기준 타입 (POSITION 또는 MEMBER)
    private SalaryStatus status;        // 급여 처리 상태

    private BigDecimal baseSalary;      // 기준 급여
    private BigDecimal hourlyRate;      // 기준 시급
    private String title;   // 급여 스냅샷용 직급/개인급여 명칭
    private Boolean active; // 급여 스냅샷용 활성 여부

    private BigDecimal hoursBaseSalary; // 초과근무 수당
    private BigDecimal grossPay;        // 총지급액
    private BigDecimal netPay;          // 실지급액

    private List<TaxDeductionDetailDto> deductions; // 공제 항목 리스트
    private BigDecimal totalDeduction;  // 총 공제액


}
