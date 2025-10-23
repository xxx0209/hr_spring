package com.hr.service;

import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDetailDto;
import com.hr.entity.Salary;

import java.util.List;
import java.util.stream.Collectors;

public class SalaryDtoConvertor {
    public static SalaryResponseDto toResponseDto(Salary salary) {
        SalaryResponseDto dto = new SalaryResponseDto();
        dto.setSalaryId(salary.getSalaryId());
        dto.setMemberId(salary.getMember().getId());
        dto.setMemberName(salary.getMember().getName());
        dto.setPayDate(salary.getPayDate());
        dto.setCustomBaseSalary(salary.getCustomBaseSalary());
        dto.setHoursBaseSalary(salary.getHoursBaseSalary());
        dto.setGrossPay(salary.getGrossPay());
        dto.setTotalDeduction(salary.getTotalDeduction());
        dto.setNetPay(salary.getNetPay());
        dto.setStatus(salary.getStatus().name());

        List<TaxDeductionDetailDto> deductionDtos = salary.getTaxDeductions().stream()
                .map(d -> {
                    TaxDeductionDetailDto td = new TaxDeductionDetailDto();
                    td.setTypeCode(d.getDeductionType().getTypeCode());
                    td.setTypeName(d.getDeductionType().getName());
                    td.setRate(d.getRate());
                    td.setAmount(d.getAmount());
                    return td;
                })
                .collect(Collectors.toList());

        dto.setDeductions(deductionDtos);
        return dto;

    }
}