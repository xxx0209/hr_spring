package com.hr.service;

import com.hr.constant.Role;
import com.hr.constant.SalaryStatus;
import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDto;
import com.hr.entity.*;
import com.hr.repository.DeductionTypeRepository;
import com.hr.repository.MemberRepository;
import com.hr.repository.SalaryRepository;
import com.hr.repository.TaxDeductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final MemberRepository memberRepository;
    private final BaseSalaryService baseSalaryService;
    private final SalaryRepository salaryRepository;
    private final DeductionTypeRepository deductionTypeRepository;
    private final TaxDeductionRepository taxDeductionRepository;

    public SalaryResponseDto createSalary(SalaryRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("직원 정보 없음"));

        LocalDate payDate = dto.getPayDate() != null ? dto.getPayDate() : getDefaultPayDate();
        if (salaryRepository.existsByMemberAndPayDate(member, payDate)) {
            throw new IllegalStateException("이미 해당 월의 급여가 생성되어 있습니다.");
        }

        Salary salary = calculateSalary(member, dto, null);
        salary.setStatus(SalaryStatus.DRAFT);
        Salary saved = salaryRepository.save(salary);
        return SalaryDtoConvertor.toResponseDto(saved);
    }

    @Transactional
    public SalaryResponseDto updateSalary(Integer salaryId, SalaryRequestDto dto) {
        Salary existing = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역 없음"));

        taxDeductionRepository.deleteAllBySalary(existing);
        existing.getTaxDeductions().clear();

        Salary updated = calculateSalary(existing.getMember(), dto, existing);
        Salary saved = salaryRepository.save(updated);
        return SalaryDtoConvertor.toResponseDto(saved);
    }

    private Salary calculateSalary(Member member, SalaryRequestDto dto, Salary existingSalary) {
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

        Map<String, TaxDeductionDto> uniqueDeductions = deductionDtos.stream()
                .collect(Collectors.toMap(TaxDeductionDto::getTypeCode, Function.identity(), (a, b) -> a));

        List<TaxDeduction> deductions = new ArrayList<>();
        for (TaxDeductionDto td : uniqueDeductions.values()) {
            DeductionType type = deductionTypeRepository.findByTypeCode(td.getTypeCode())
                    .orElseThrow(() -> new IllegalArgumentException("공제 유형 없음: " + td.getTypeCode()));

            BigDecimal rate = td.getRate() != null ? td.getRate() : type.getDefaultRate();
            if (rate == null) throw new IllegalArgumentException("공제율이 정의되지 않았습니다: " + td.getTypeCode());

            BigDecimal amount = grossPay.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            totalDeduction = totalDeduction.add(amount);

            TaxDeduction deduction = new TaxDeduction();
            deduction.setDeductionType(type);
            deduction.setRate(rate);
            deduction.setAmount(amount);
            deduction.setSalary(existingSalary);
            deductions.add(deduction);
        }

        Salary salary = existingSalary != null ? existingSalary : new Salary();
        salary.setMember(member);
        salary.setPayDate(dto.getPayDate() != null ? dto.getPayDate() : getDefaultPayDate());
        salary.setCustomBaseSalary(baseAmount);
        salary.setHoursBaseSalary(overtimePay);
        salary.setGrossPay(grossPay);
        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(grossPay.subtract(totalDeduction));
        salary.setTaxDeductions(deductions);

        for (TaxDeduction deduction : deductions) {
            deduction.setSalary(salary);
        }

        return salary;
    }


    private LocalDate getDefaultPayDate() {
        return LocalDate.now().withDayOfMonth(20);
    }

    /**
     * 직원별 급여 이력 조회
     */
    @Transactional(readOnly = true)
    public List<SalaryResponseDto> getSalaryHistoryByMember(String memberId) {
        return salaryRepository.findByMember_IdOrderByPayDateDesc(memberId).stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }
    /**
     * 월별 급여 조회 (본인 또는 관리자)
     */
    public List<SalaryResponseDto> getMonthlySalaries(String memberId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return salaryRepository.findByMember_IdAndPayDateBetween(memberId, start, end).stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 급여 단건 상세 조회
     */
    public SalaryResponseDto getSalaryDetail(Integer salaryId, String requesterId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역 없음"));

        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보 없음"));

        if (requester.getRole() != Role.ADMIN && !salary.getMember().getId().equals(requesterId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        return SalaryDtoConvertor.toResponseDto(salary);
    }

    /**
     * 관리자 전체 급여 조회 (월별)
     */
    public List<SalaryResponseDto> getAllSalariesByMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return salaryRepository.findByPayDateBetween(start, end).stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }



    /**
     * 급여 상태 변경
     */
    @Transactional
    public void updateSalaryStatus(Integer salaryId, SalaryStatus status) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역 없음"));
        salary.setStatus(status);
    }

    @Transactional(readOnly = true)
    public List<SalaryResponseDto> getSalariesByStatus(SalaryStatus status) {
        List<Salary> salaries = salaryRepository.findByStatus(status);
        return salaries.stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }

}

