package com.hr.service;

import com.hr.constant.SalaryStatus;
import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDto;
import com.hr.entity.*;
import com.hr.repository.DeductionTypeRepository;
import com.hr.repository.MemberRepository;
import com.hr.repository.SalaryRepository;
import com.hr.repository.TaxDeductionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final MemberRepository memberRepository;
    private final BaseSalaryService baseSalaryService;
    private final SalaryRepository salaryRepository;
    private final DeductionTypeRepository deductionTypeRepository;
    private final TaxDeductionRepository taxDeductionRepository;

    /**
     * 급여 생성
     */
    @Transactional
    public SalaryResponseDto createSalary(SalaryRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("직원 정보 없음"));

        BaseSalary baseSalary = baseSalaryService.getSalaryForMember(member);
        BigDecimal baseAmount = baseSalary.getBaseSalary();
        BigDecimal hourlyRate = baseSalary.getHourlyRate();

        BigDecimal overtimePay = BigDecimal.ZERO;
        if (dto.getOvertimeHours() != null && hourlyRate != null && hourlyRate.compareTo(BigDecimal.ZERO) > 0) {
            overtimePay = hourlyRate.multiply(new BigDecimal("1.5"))
                    .multiply(dto.getOvertimeHours())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal grossPay = baseAmount.add(overtimePay);
        BigDecimal totalDeduction = BigDecimal.ZERO;

        // 공제 항목: 요청이 없으면 DeductionType 전체를 기본으로 사용
        List<TaxDeductionDto> deductionDtos = dto.getDeductions();
        if (deductionDtos == null || deductionDtos.isEmpty()) {
            deductionDtos = deductionTypeRepository.findAll().stream()
                    .map(type -> new TaxDeductionDto(type.getTypeCode(), null))
                    .collect(Collectors.toList());
        }

        List<TaxDeduction> deductions = new ArrayList<>();
        for (TaxDeductionDto td : deductionDtos) {
            DeductionType type = deductionTypeRepository.findByTypeCode(td.getTypeCode())
                    .orElseThrow(() -> new IllegalArgumentException("공제 유형 없음: " + td.getTypeCode()));

            BigDecimal rate = td.getRate() != null ? td.getRate() : type.getDefaultRate();
            BigDecimal amount = grossPay.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            totalDeduction = totalDeduction.add(amount);

            TaxDeduction deduction = new TaxDeduction();
            deduction.setDeductionType(type);
            deduction.setRate(rate);
            deduction.setAmount(amount);
            deduction.setSalary(null); // 나중에 연결
            deductions.add(deduction);
        }

        Salary salary = new Salary();
        salary.setMember(member);
        salary.setPayDate(dto.getPayDate());
        salary.setCustomBaseSalary(baseAmount);
        salary.setHoursBaseSalary(overtimePay);
        salary.setGrossPay(grossPay);
        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(grossPay.subtract(totalDeduction));
        salary.setStatus(SalaryStatus.COMPLETED);

        for (TaxDeduction deduction : deductions) {
            deduction.setSalary(salary);
        }
        salary.setTaxDeductions(deductions);

        Salary saved = salaryRepository.save(salary);
        return SalaryDtoConvertor.toResponseDto(saved);
    }

    /**
     * 급여 수정
     */
    @Transactional
    public SalaryResponseDto updateSalary(Integer salaryId, SalaryRequestDto dto) {
        Salary existing = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역 없음"));

        taxDeductionRepository.deleteAllBySalary(existing);
        existing.getTaxDeductions().clear();

        Member member = existing.getMember();
        BaseSalary baseSalary = baseSalaryService.getSalaryForMember(member);
        BigDecimal baseAmount = baseSalary.getBaseSalary();
        BigDecimal hourlyRate = baseSalary.getHourlyRate();

        BigDecimal overtimePay = BigDecimal.ZERO;
        if (dto.getOvertimeHours() != null && hourlyRate != null && hourlyRate.compareTo(BigDecimal.ZERO) > 0) {
            overtimePay = hourlyRate.multiply(new BigDecimal("1.5"))
                    .multiply(dto.getOvertimeHours())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal grossPay = baseAmount.add(overtimePay);
        BigDecimal totalDeduction = BigDecimal.ZERO;

        List<TaxDeductionDto> deductionDtos = dto.getDeductions();
        if (deductionDtos == null || deductionDtos.isEmpty()) {
            deductionDtos = deductionTypeRepository.findAll().stream()
                    .map(type -> new TaxDeductionDto(type.getTypeCode(), null))
                    .collect(Collectors.toList());
        }

        List<TaxDeduction> newDeductions = new ArrayList<>();
        for (TaxDeductionDto td : deductionDtos) {
            DeductionType type = deductionTypeRepository.findByTypeCode(td.getTypeCode())
                    .orElseThrow(() -> new IllegalArgumentException("공제 유형 없음: " + td.getTypeCode()));

            BigDecimal rate = td.getRate() != null ? td.getRate() : type.getDefaultRate();
            BigDecimal amount = grossPay.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            totalDeduction = totalDeduction.add(amount);

            TaxDeduction deduction = new TaxDeduction();
            deduction.setSalary(existing);
            deduction.setDeductionType(type);
            deduction.setRate(rate);
            deduction.setAmount(amount);
            newDeductions.add(deduction);
        }

        existing.setPayDate(dto.getPayDate());
        existing.setCustomBaseSalary(baseAmount);
        existing.setHoursBaseSalary(overtimePay);
        existing.setGrossPay(grossPay);
        existing.setTotalDeduction(totalDeduction);
        existing.setNetPay(grossPay.subtract(totalDeduction));
        existing.setStatus(SalaryStatus.COMPLETED);
        existing.setTaxDeductions(newDeductions);

        Salary saved = salaryRepository.save(existing);
        return SalaryDtoConvertor.toResponseDto(saved);
    }

    /**
     * 직원별 급여 이력 조회
     */
    @Transactional(readOnly = true)
    public List<SalaryResponseDto> getSalaryHistoryByMember(String memberId) {
        List<Salary> salaries = salaryRepository.findByMember_Id(memberId);
        return salaries.stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }
}

