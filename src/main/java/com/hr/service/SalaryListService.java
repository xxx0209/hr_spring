package com.hr.service;

import com.hr.dto.*;
import com.hr.entity.Salary;
import com.hr.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryListService {

    private final SalaryRepository salaryRepository;

    public List<SalaryResponseDto> getSalariesForMember(String memberId) {
        return salaryRepository.findByMemberId(memberId).stream()
                .map(this::toResponseDto).toList();
    }

    public List<SalaryResponseDto> getSalariesByPeriod(String memberId, LocalDate startDate, LocalDate endDate) {
        return salaryRepository.findByMemberIdAndPayDateBetween(memberId, startDate, endDate).stream()
                .map(this::toResponseDto).toList();
    }

    private SalaryResponseDto toResponseDto(Salary salary) {
        List<TaxDeductionDetailDto> deductionDetails = salary.getTaxDeductions().stream().map(d -> {
            TaxDeductionDetailDto dto = new TaxDeductionDetailDto();
            dto.setTypeCode(d.getDeductionType().getTypeCode());
            dto.setTypeName(d.getDeductionType().getTypeName());
            dto.setRate(d.getRate());
            dto.setAmount(d.getAmount());
            return dto;
        }).toList();

        SalaryResponseDto response = new SalaryResponseDto();
        response.setSalaryId(salary.getId());
        response.setMemberId(salary.getMember().getId());
        response.setPayDate(salary.getPayDate());
        response.setBaseSalary(salary.getBaseSalary());
        response.setOvertimePay(salary.getOvertimePay());
        response.setGrossPay(salary.getGrossPay());
        response.setTotalDeduction(salary.getTotalDeduction());
        response.setNetPay(salary.getNetPay());
        response.setStatus(salary.getStatus());
        response.setDeductions(deductionDetails);

        return response;
    }
}
