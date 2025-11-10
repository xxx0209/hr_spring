package com.hr.service;

import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDetailDto;
import com.hr.entity.Salary;
import com.hr.entity.PositionSalary;
import com.hr.entity.MemberSalary;
import com.hr.constant.SalaryType;

import java.util.List;
import java.util.stream.Collectors;

public class SalaryDtoConvertor {

    public static SalaryResponseDto toResponseDto(Salary salary) {
        SalaryResponseDto dto = new SalaryResponseDto();
        dto.setSalaryId(salary.getSalaryId());
        dto.setMemberId(salary.getMember().getId());
        dto.setMemberName(salary.getMember().getName());
//        dto.setSalaryMonth(salary.getSalaryMonth());
        dto.setPayDate(salary.getPayDate());
        dto.setSalaryType(salary.getSalaryType());
        dto.setStatus(salary.getStatus());

        // 기준 급여 정보 설정
        if (salary.getSalaryType() == SalaryType.MEMBER && salary.getMemberSalary() != null) {
            MemberSalary ms = salary.getMemberSalary();
            dto.setBaseSalary(ms.getBaseSalary());
            dto.setHourlyRate(ms.getHourlyRate());
        } else if (salary.getSalaryType() == SalaryType.POSITION && salary.getPositionSalary() != null) {
            PositionSalary ps = salary.getPositionSalary();
            dto.setBaseSalary(ps.getBaseSalary());
            dto.setHourlyRate(ps.getHourlyRate());
//            dto.setPositionName(ps.getPosition().getPositionName());
        }

        dto.setHoursBaseSalary(salary.getHoursBaseSalary());
        dto.setGrossPay(salary.getGrossPay());
        dto.setTotalDeduction(salary.getTotalDeduction());
        dto.setNetPay(salary.getNetPay());

        List<TaxDeductionDetailDto> deductionDtos = salary.getTaxDeductions().stream()
                .map(d -> {
                    TaxDeductionDetailDto td = new TaxDeductionDetailDto();
                    td.setTypeCode(d.getDeductionType().getTypeCode());
                    td.setTypeName(d.getDeductionType().getName());
                    td.setRate(d.getRate());
                    td.setAmount(d.getAmount());
                    return td;
                }).collect(Collectors.toList());

        dto.setDeductions(deductionDtos);
        return dto;
    }
}
