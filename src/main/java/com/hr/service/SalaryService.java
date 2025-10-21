package com.hr.service;

import com.hr.dto.*;
import com.hr.entity.*;
import com.hr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final MemberRepository memberRepository;
    private final DeductionTypeRepository deductionTypeRepository;
    private final SalaryRepository salaryRepository;
    private final TaxDeductionRepository taxDeductionRepository;

    @Transactional
    public SalaryResponseDto calculateAndSaveSalary(SalaryRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직원입니다."));

        BigDecimal baseSalary = member.getCustomBaseSalary() != null
                ? member.getCustomBaseSalary()
                : member.getPosition().getDefaultSalary();

        BigDecimal overtimePay = requestDto.getOvertimePay() != null
                ? requestDto.getOvertimePay()
                : BigDecimal.ZERO;

        BigDecimal grossPay = baseSalary.add(overtimePay);

        List<TaxDeductionDto> deductionDtos = requestDto.getDeductions() != null
                ? requestDto.getDeductions()
                : Collections.emptyList();

        List<TaxDeduction> deductions = new ArrayList<>();
        BigDecimal totalDeduction = BigDecimal.ZERO;

        for (TaxDeductionDto dto : deductionDtos) {
            DeductionType type = deductionTypeRepository.findById(dto.getTypeCode())
                    .orElseThrow(() -> new IllegalArgumentException("공제 유형이 존재하지 않습니다: " + dto.getTypeCode()));

            BigDecimal rate = dto.getRate() != null ? dto.getRate() : type.getDefaultRate();
            BigDecimal amount = grossPay.multiply(rate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            TaxDeduction deduction = new TaxDeduction();
            deduction.setDeductionType(type);
            deduction.setRate(rate);
            deduction.setAmount(amount);
            deductions.add(deduction);

            totalDeduction = totalDeduction.add(amount);
        }

        BigDecimal netPay = grossPay.subtract(totalDeduction);

        Salary salary = new Salary();
        salary.setMember(member);
        salary.setPayDate(LocalDate.now().withDayOfMonth(20)); // 매달 20일 고정
        salary.setBaseSalary(baseSalary);
        salary.setOvertimePay(overtimePay);
        salary.setGrossPay(grossPay);
        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(netPay);
        salary.setStatus("지급대기");

        deductions.forEach(d -> d.setSalary(salary));
        salary.setTaxDeductions(deductions);

        Salary savedSalary = salaryRepository.save(salary);
        taxDeductionRepository.saveAll(deductions);

        return toResponseDto(savedSalary);
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
