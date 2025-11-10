package com.hr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class PositionSalaryDto {

    private Long id; // id
    private Long positionId; // positionId
    private String positionName;
    private String title;
    private BigDecimal baseSalary;
    private BigDecimal hourlyRate;
    private Boolean active = true;
}
