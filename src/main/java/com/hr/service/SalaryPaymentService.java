package com.hr.service;

import com.hr.dto.*;
import com.hr.entity.Salary;
import com.hr.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryPaymentService {

    private final SalaryRepository salaryRepository;

    @Transactional
    public SalaryResponseDto markSalaryAsPaid(Integer salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역을 찾을 수 없습니다."));

        salary.setStatus("지급완료");
        Salary updated = salaryRepository.save(salary);

        return toResponseDto(updated);
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
