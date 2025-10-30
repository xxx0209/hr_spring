package com.hr.service;

import com.hr.constant.MemberRole;
import com.hr.constant.SalaryStatus;
import com.hr.dto.MemberDto;
import com.hr.dto.SalaryRequestDto;
import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDto;
import com.hr.entity.*;
import com.hr.repository.DeductionTypeRepository;
import com.hr.repository.MemberRepository;
import com.hr.repository.SalaryRepository;
import com.hr.repository.TaxDeductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
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

    // 지급일 계산 : 매월 20일
    private LocalDate getDefaultPayDate(YearMonth month) {
        return month.atDay(20);
    }

    // 🔹 급여 생성
    public SalaryResponseDto createSalary(SalaryRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("직원 정보 없음"));

        YearMonth salaryMonth = dto.getSalaryMonth();
        if (salaryMonth == null) {
            throw new IllegalArgumentException("급여 대상 월이 누락되었습니다.");
        }

        LocalDate payDate = getDefaultPayDate(salaryMonth);

        if (salaryRepository.existsByMemberAndSalaryMonth(member, salaryMonth)) {
            throw new IllegalStateException("이미 생성된 급여가 있습니다.");
        }

        Salary salary = calculateSalary(member, dto, salaryMonth, payDate, null);
        salary.setStatus(SalaryStatus.DRAFT);
        Salary saved = salaryRepository.save(salary);
        return SalaryDtoConvertor.toResponseDto(saved);
    }

    // 🔹 급여 수정
    @Transactional
    public SalaryResponseDto updateSalary(Integer salaryId, SalaryRequestDto dto) {
        Salary existing = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역 없음"));

        taxDeductionRepository.deleteAllBySalary(existing);
        existing.getTaxDeductions().clear();

        YearMonth salaryMonth = dto.getSalaryMonth();
        LocalDate payDate = getDefaultPayDate(salaryMonth);

        Salary updated = calculateSalary(existing.getMember(), dto, salaryMonth, payDate, existing);
        Salary saved = salaryRepository.save(updated);
        return SalaryDtoConvertor.toResponseDto(saved);
    }

    // 🔹 급여 계산 로직
    private Salary calculateSalary(Member member, SalaryRequestDto dto, YearMonth salaryMonth, LocalDate payDate, Salary existingSalary) {
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

        List<TaxDeductionDto> deductionDtos = Optional.ofNullable(dto.getDeductions())
                .orElseGet(() -> deductionTypeRepository.findAll().stream()
                        .map(type -> new TaxDeductionDto(type.getTypeCode(), null))
                        .collect(Collectors.toList()));

        Map<String, TaxDeductionDto> uniqueDeductions = deductionDtos.stream()
                .collect(Collectors.toMap(TaxDeductionDto::getTypeCode, Function.identity(), (a, b) -> a));

        List<TaxDeduction> deductions = new ArrayList<>();
        for (TaxDeductionDto td : uniqueDeductions.values()) {
            DeductionType type = deductionTypeRepository.findByTypeCode(td.getTypeCode())
                    .orElseThrow(() -> new IllegalArgumentException("공제 유형 없음: " + td.getTypeCode()));

            BigDecimal rate = Optional.ofNullable(td.getRate()).orElse(type.getDefaultRate());
            if (rate == null) {
                rate = BigDecimal.ZERO;
            }

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
        salary.setSalaryMonth(salaryMonth);
        salary.setPayDate(payDate);
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

    // 🔹 급여 상세 조회
    public SalaryResponseDto getSalaryDetail(Integer salaryId, String requesterId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역 없음"));

        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보 없음"));

        if (requester.getMemberRole() != MemberRole.ROLE_ADMIN && !salary.getMember().getId().equals(requesterId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        return SalaryDtoConvertor.toResponseDto(salary);
    }

    // 🔹 직원 급여 이력 (비페이징)
    public List<SalaryResponseDto> getSalaryHistoryByMember(String memberId) {
        return salaryRepository.findByMember_IdOrderByPayDateDesc(memberId).stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }

    // 🔹 직원 월별 급여 조회 (COMPLETED만)
    public List<SalaryResponseDto> getMonthlyCompletedSalaries(String memberId, int year, int month) {
        YearMonth salaryMonth = YearMonth.of(year, month);
        return salaryRepository.findByMember_IdAndSalaryMonthAndStatus(
                memberId, salaryMonth, SalaryStatus.COMPLETED
        ).stream().map(SalaryDtoConvertor::toResponseDto).collect(Collectors.toList());
    }

    // 🔹 관리자용 승인된 급여 검색 (회원별 + 월별 조건)
    public List<SalaryResponseDto> searchCompletedSalaries(String memberId, YearMonth salaryMonth) {
        List<Salary> salaries;

        if (memberId != null && salaryMonth != null) {
            salaries = salaryRepository.findByMember_IdAndSalaryMonthAndStatus(memberId, salaryMonth, SalaryStatus.COMPLETED);
        } else if (memberId != null) {
            salaries = salaryRepository.findByMember_IdAndStatusOrderByPayDateDesc(memberId, SalaryStatus.COMPLETED);
        } else if (salaryMonth != null) {
            salaries = salaryRepository.findBySalaryMonthAndStatus(salaryMonth, SalaryStatus.COMPLETED);
        } else {
            salaries = salaryRepository.findByStatus(SalaryStatus.COMPLETED);
        }

        return salaries.stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }


    // 🔹 직원 월별 급여 조회 (페이징)
    public Page<SalaryResponseDto> getPagedMonthlySalaries(String memberId, YearMonth month, SalaryStatus status, Pageable pageable) {
        return salaryRepository.findByMember_IdAndSalaryMonthAndStatus(memberId, month, status, pageable)
                .map(SalaryDtoConvertor::toResponseDto);
    }

    // 🔹 급여 상태 변경
    @Transactional
    public void updateSalaryStatus(Integer salaryId, SalaryStatus status) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("급여 내역 없음"));
        salary.setStatus(status);
    }

    // 🔹 상태별 급여 목록 조회 (비페이징)
    public List<SalaryResponseDto> getSalariesByStatus(SalaryStatus status) {
        return salaryRepository.findByStatus(status).stream()
                .map(SalaryDtoConvertor::toResponseDto)
                .collect(Collectors.toList());
    }
    // 급여 삭제
    @Transactional
    public void deleteSalary(Integer salaryId, MemberDto requester) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 급여가 존재하지 않습니다."));

        // 권한 체크: 관리자 또는 본인만 삭제 가능
        if (requester.getMemberRole() != MemberRole.ROLE_ADMIN &&
                !salary.getMember().getId().equals(requester.getId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // 승인된 급여는 삭제 불가
        if (salary.getStatus() != SalaryStatus.DRAFT) {
            throw new IllegalStateException("승인된 급여는 삭제할 수 없습니다.");
        }

        salaryRepository.delete(salary);
    }

}
