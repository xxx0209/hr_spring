package com.hr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class MemberSalaryDto {
    private Long id;
    private String memberId; // Member 엔티티의 ID
    private String memberName;
    private BigDecimal baseSalary;
    private BigDecimal hourlyRate;

}
