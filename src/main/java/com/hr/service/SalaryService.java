package com.hr.service;

import com.hr.constant.SalaryStatus;
import com.hr.constant.SalaryType;
import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDetailDto;
import com.hr.entity.*;
import com.hr.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryService {

    private final MemberRepository memberRepository;
    private final SalaryRepository salaryRepository;
    private final MemberSalaryRepository memberSalaryRepository;
    private final PositionSalaryRepository positionSalaryRepository;
    private final DeductionTypeRepository deductionTypeRepository;
    private final TaxDeductionRepository taxDeductionRepository;

    // ======================
    // 급여 생성 + 공제 계산
    // ======================
    public SalaryResponseDto create(SalaryResponseDto dto) {

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        YearMonth salaryMonth = dto.getSalaryMonth() != null
                ? YearMonth.parse(dto.getSalaryMonth())
                : YearMonth.now();

        boolean exists = salaryRepository.existsByMemberAndSalaryMonth(member, salaryMonth);
        if (exists) throw new RuntimeException("해당 급여 월에 이미 급여가 존재합니다.");

        Salary salary = new Salary();
        salary.setMember(member);
        salary.setSalaryMonth(salaryMonth);
        salary.setPayDate(LocalDate.of(salaryMonth.getYear(), salaryMonth.getMonth(), 20));
        salary.setStatus(SalaryStatus.DRAFT);

        // 급여 기준 조회
        BigDecimal baseSalary = BigDecimal.ZERO;
        BigDecimal hourlyRate = BigDecimal.ZERO;

        MemberSalary memberSalary = memberSalaryRepository.findByMember_Id(member.getId()).orElse(null);

        if (memberSalary != null) {
            salary.setSalaryType(SalaryType.MEMBER);
            salary.setMemberSalary(memberSalary);
            baseSalary = memberSalary.getBaseSalary() != null ? memberSalary.getBaseSalary() : BigDecimal.ZERO;
            hourlyRate = memberSalary.getHourlyRate() != null ? memberSalary.getHourlyRate() : BigDecimal.ZERO;
        } else {
            if (member.getPosition() == null)
                throw new RuntimeException("개인급여가 없고, 회원의 직급이 존재하지 않습니다.");

            List<PositionSalary> positionSalaries = positionSalaryRepository
                    .findByPosition_PositionIdAndActiveTrue(member.getPosition().getPositionId());

            if (positionSalaries.isEmpty())
                throw new RuntimeException("개인급여가 없고, 직급에 등록된 급여가 없습니다.");

            PositionSalary ps = positionSalaries.get(0);
            salary.setSalaryType(SalaryType.POSITION);
            salary.setPositionSalary(ps);
            baseSalary = ps.getBaseSalary() != null ? ps.getBaseSalary() : BigDecimal.ZERO;
            hourlyRate = ps.getHourlyRate() != null ? ps.getHourlyRate() : BigDecimal.ZERO;

            salary.setTitle(ps.getTitle());
            salary.setActive(ps.getActive());
        }

        salary.setBaseSalary(baseSalary);
        salary.setHourlyRate(hourlyRate);

        BigDecimal hoursBaseSalary = dto.getHoursBaseSalary() != null ? dto.getHoursBaseSalary() : BigDecimal.ZERO;
        salary.setHoursBaseSalary(hoursBaseSalary);

        BigDecimal grossPay = baseSalary.add(hoursBaseSalary);
        salary.setGrossPay(grossPay);

        List<DeductionType> deductionTypes = deductionTypeRepository.findAll();
        BigDecimal totalDeduction = BigDecimal.ZERO;

        for (DeductionType type : deductionTypes) {
            BigDecimal rate = type.getDefaultRate() != null ? type.getDefaultRate() : BigDecimal.ZERO;
            BigDecimal amount = grossPay.multiply(rate).setScale(0, BigDecimal.ROUND_HALF_UP);

            TaxDeduction td = new TaxDeduction();
            td.setSalary(salary);
            td.setDeductionType(type);
            td.setRate(rate);
            td.setAmount(amount);

            salary.getTaxDeductions().add(td);
            totalDeduction = totalDeduction.add(amount);
        }

        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(grossPay.subtract(totalDeduction));

        Salary saved = salaryRepository.save(salary);
        return convertResponse(saved);
    }

    // ======================
    // 급여 수정
    // ======================
    public SalaryResponseDto updateAndRecalculate(Integer salaryId, SalaryResponseDto dto) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("급여가 존재하지 않습니다."));

        if (salary.getStatus() == SalaryStatus.COMPLETED)
            throw new RuntimeException("지급 완료된 급여는 수정할 수 없습니다.");
        if (salary.getSalaryType() != SalaryType.POSITION)
            throw new RuntimeException("개인 급여는 수정할 수 없습니다.");

        if (dto.getSalaryMonth() != null) {
            YearMonth salaryMonth = YearMonth.parse(dto.getSalaryMonth());
            salary.setSalaryMonth(salaryMonth);
            salary.setPayDate(LocalDate.of(salaryMonth.getYear(), salaryMonth.getMonth(), 20));
        }

        if (dto.getTitle() != null) salary.setTitle(dto.getTitle());
        if (dto.getBaseSalary() != null) salary.setBaseSalary(dto.getBaseSalary());
        if (dto.getHourlyRate() != null) salary.setHourlyRate(dto.getHourlyRate());

        BigDecimal hoursBaseSalary = salary.getHoursBaseSalary() != null ? salary.getHoursBaseSalary() : BigDecimal.ZERO;
        BigDecimal grossPay = salary.getBaseSalary().add(hoursBaseSalary);
        salary.setGrossPay(grossPay);

        List<DeductionType> deductionTypes = deductionTypeRepository.findAll();
        BigDecimal totalDeduction = BigDecimal.ZERO;
        salary.getTaxDeductions().clear();

        for (DeductionType type : deductionTypes) {
            BigDecimal rate = type.getDefaultRate() != null ? type.getDefaultRate() : BigDecimal.ZERO;
            BigDecimal amount = grossPay.multiply(rate).setScale(0, BigDecimal.ROUND_HALF_UP);

            TaxDeduction td = new TaxDeduction();
            td.setSalary(salary);
            td.setDeductionType(type);
            td.setRate(rate);
            td.setAmount(amount);

            salary.getTaxDeductions().add(td);
            totalDeduction = totalDeduction.add(amount);
        }

        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(grossPay.subtract(totalDeduction));

        Salary saved = salaryRepository.save(salary);
        return convertResponse(saved);
    }

    // ======================
    // 승인
    // ======================
    public SalaryResponseDto approve(Integer salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("해당 급여가 존재하지 않습니다."));
        salary.setStatus(SalaryStatus.COMPLETED);
        salary.setPayDate(LocalDate.of(salary.getSalaryMonth().getYear(), salary.getSalaryMonth().getMonth(), 20));
        salaryRepository.save(salary);
        return convertResponse(salary);
    }

    // ======================
    // 조회 (페이징 + 검색)
    // ======================
    public Page<SalaryResponseDto> findAll(Pageable pageable, String searchMemberName, String salaryMonthStr) {
        Page<Salary> page;

        if ((searchMemberName == null || searchMemberName.isBlank()) && (salaryMonthStr == null || salaryMonthStr.isBlank())) {
            page = salaryRepository.findAll(pageable);
        } else if (searchMemberName != null && !searchMemberName.isBlank() && (salaryMonthStr == null || salaryMonthStr.isBlank())) {
            page = salaryRepository.findByMember_NameContainingIgnoreCase(searchMemberName, pageable);
        } else if ((searchMemberName == null || searchMemberName.isBlank()) && salaryMonthStr != null && !salaryMonthStr.isBlank()) {
            YearMonth ym = YearMonth.parse(salaryMonthStr);
            page = salaryRepository.findBySalaryMonth(ym, pageable);
        } else {
            YearMonth ym = YearMonth.parse(salaryMonthStr);
            page = salaryRepository.findByMember_NameContainingIgnoreCaseAndSalaryMonth(searchMemberName, ym, pageable);
        }

        return page.map(this::convertResponse);
    }
    // 미승인 급여 조회 (DRAFT)
    public Page<SalaryResponseDto> findDraftSalaries(Pageable pageable, String searchMemberName) {
        Page<Salary> page;
        if (searchMemberName == null || searchMemberName.isBlank()) {
            page = salaryRepository.findByStatus(SalaryStatus.DRAFT, pageable);
        } else {
            page = salaryRepository.findByStatusAndMember_NameContainingIgnoreCase(SalaryStatus.DRAFT, searchMemberName, pageable);
        }
        return page.map(this::convertResponse);
    }

    // 승인 급여 조회 (COMPLETED)
    public Page<SalaryResponseDto> findCompletedSalariesFiltered(Pageable pageable, String memberId, String salaryMonthStr) {
        Page<Salary> page;
        if ((memberId == null || memberId.isBlank()) && (salaryMonthStr == null || salaryMonthStr.isBlank())) {
            page = salaryRepository.findByStatus(SalaryStatus.COMPLETED, pageable);
        } else if (memberId != null && !memberId.isBlank() && (salaryMonthStr == null || salaryMonthStr.isBlank())) {
            page = salaryRepository.findByStatusAndMember_Id(SalaryStatus.COMPLETED, memberId, pageable);
        } else if ((memberId == null || memberId.isBlank()) && salaryMonthStr != null && !salaryMonthStr.isBlank()) {
            YearMonth ym = YearMonth.parse(salaryMonthStr);
            page = salaryRepository.findByStatusAndSalaryMonth(SalaryStatus.COMPLETED, ym, pageable);
        } else {
            YearMonth ym = YearMonth.parse(salaryMonthStr);
            page = salaryRepository.findByStatusAndMember_IdAndSalaryMonth(SalaryStatus.COMPLETED, memberId, ym, pageable);
        }
        return page.map(this::convertResponse);
    }

    // 나의 급여 내역 (페이징 없이 전체)
    public List<SalaryResponseDto> findByMemberId(String memberId) {
        return salaryRepository.findByMember_IdAndStatusOrderByPayDateDesc(memberId, SalaryStatus.COMPLETED)
                .stream().map(this::convertResponse).collect(Collectors.toList());
    }

    // 나의 급여 상세 (페이징 없음)
    public SalaryResponseDto findMySalaryDetail(String memberId, Integer salaryId) {
        Salary salary = salaryRepository.findBySalaryIdAndMemberId(salaryId, memberId)
                .orElseThrow(() -> new RuntimeException("해당 급여 내역을 찾을 수 없습니다."));
        return convertResponse(salary);
    }

    // 삭제
    public void delete(Integer salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("삭제할 급여가 존재하지 않습니다."));
        if (salary.getStatus() == SalaryStatus.COMPLETED) {
            throw new RuntimeException("지급 완료된 급여는 삭제할 수 없습니다.");
        }
        salaryRepository.delete(salary);
    }

    // ======================
    // DTO 변환
    // ======================
    private SalaryResponseDto convertResponse(Salary s) {
        SalaryResponseDto dto = new SalaryResponseDto();
        dto.setSalaryId(s.getSalaryId());
        dto.setMemberId(s.getMember().getId());
        dto.setMemberName(s.getMember().getName());
        dto.setSalaryMonth(s.getSalaryMonth().toString());
        dto.setPayDate(s.getPayDate());
        dto.setSalaryType(s.getSalaryType());
        dto.setStatus(s.getStatus());
        dto.setGrossPay(s.getGrossPay());
        dto.setTotalDeduction(s.getTotalDeduction());
        dto.setHoursBaseSalary(s.getHoursBaseSalary());
        dto.setNetPay(s.getNetPay());
        dto.setBaseSalary(s.getBaseSalary());
        dto.setHourlyRate(s.getHourlyRate());
        dto.setTitle(s.getTitle());
        dto.setActive(s.getActive());

        // POSITION인 경우 positionSalary의 title을 DTO에 넣어주기
        if (s.getSalaryType() == SalaryType.POSITION && s.getPositionSalary() != null) {
            dto.setTitle(s.getPositionSalary().getTitle());
        } else {
            dto.setTitle(s.getTitle()); // MEMBER 타입은 기존 title 사용
        }


        if (s.getTaxDeductions() != null && !s.getTaxDeductions().isEmpty()) {
            List<TaxDeductionDetailDto> deductionList = s.getTaxDeductions().stream().map(td -> {
                TaxDeductionDetailDto detail = new TaxDeductionDetailDto();
                detail.setTypeCode(td.getDeductionType().getTypeCode());
                detail.setTypeName(td.getDeductionType().getName());
                detail.setRate(td.getRate());
                detail.setAmount(td.getAmount());
                return detail;
            }).toList();
            dto.setDeductions(deductionList);
        }

        return dto;
    }
}
