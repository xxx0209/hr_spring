package com.hr.service;

import com.hr.constant.SalaryStatus;
import com.hr.constant.SalaryType;
import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDetailDto;
import com.hr.entity.*;
import com.hr.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

        // 1️⃣ 회원 조회
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 2️⃣ 급여월 중복 확인
        YearMonth salaryMonth = dto.getSalaryMonth() != null
                ? YearMonth.parse(dto.getSalaryMonth())
                : YearMonth.now();

        boolean exists = salaryRepository.existsByMemberAndSalaryMonth(member, salaryMonth);
        if (exists) {
            throw new RuntimeException("해당 급여 월에 이미 급여가 존재합니다.");
        }

        Salary salary = new Salary();
        salary.setMember(member);
        salary.setSalaryMonth(salaryMonth);
        salary.setPayDate(LocalDate.of(salaryMonth.getYear(), salaryMonth.getMonth(), 20));
        salary.setStatus(SalaryStatus.DRAFT);

        // 3️⃣ 급여 기준 조회 (개인급여 → 직급급여 순)
        BigDecimal baseSalary = BigDecimal.ZERO;
        BigDecimal hourlyRate = BigDecimal.ZERO;

        MemberSalary memberSalary = memberSalaryRepository.findByMember_Id(member.getId()).orElse(null);

        if (memberSalary != null) {
            salary.setSalaryType(SalaryType.MEMBER);
            salary.setMemberSalary(memberSalary);
            baseSalary = memberSalary.getBaseSalary() != null ? memberSalary.getBaseSalary() : BigDecimal.ZERO;
            hourlyRate = memberSalary.getHourlyRate() != null ? memberSalary.getHourlyRate() : BigDecimal.ZERO;

        } else {
            if (member.getPosition() == null) {
                throw new RuntimeException("개인급여가 없고, 회원의 직급이 존재하지 않습니다.");
            }
            List<PositionSalary> positionSalaries = positionSalaryRepository
                    .findByPosition_PositionIdAndActiveTrue(member.getPosition().getPositionId());
            if (positionSalaries.isEmpty()) {
                throw new RuntimeException("개인급여가 없고, 직급에 등록된 급여가 없습니다.");
            }
            PositionSalary ps = positionSalaries.get(0);
            salary.setSalaryType(SalaryType.POSITION);
            salary.setPositionSalary(ps);
            baseSalary = ps.getBaseSalary() != null ? ps.getBaseSalary() : BigDecimal.ZERO;
            hourlyRate = ps.getHourlyRate() != null ? ps.getHourlyRate() : BigDecimal.ZERO;

            // PositionSalary 스냅샷
            salary.setTitle(ps.getTitle());
            salary.setActive(ps.getActive());
        }

        // 💾 스냅샷으로 저장 (과거 급여 금액 고정)
        salary.setBaseSalary(baseSalary);
        salary.setHourlyRate(hourlyRate);

        // 4️⃣ 시급/추가급여 처리
        BigDecimal hoursBaseSalary = dto.getHoursBaseSalary() != null ? dto.getHoursBaseSalary() : BigDecimal.ZERO;
        salary.setHoursBaseSalary(hoursBaseSalary);

        // 5️⃣ 총 급여 계산
        BigDecimal grossPay = baseSalary.add(hoursBaseSalary);
        salary.setGrossPay(grossPay);

        // 6️⃣ 공제 계산
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

        // 7️⃣ 순급여 계산
        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(grossPay.subtract(totalDeduction));

        // 8️⃣ 저장
        Salary saved = salaryRepository.save(salary);

        return convertResponse(saved);
    }

    // ======================
    // 급여 수정
    // ======================
    // 급여 수정 (PositionSalary는 수정하지 않고 Salary만 수정)
    public SalaryResponseDto updateAndRecalculate(Integer salaryId, SalaryResponseDto dto) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("급여가 존재하지 않습니다."));

        if (salary.getStatus() == SalaryStatus.COMPLETED) {
            throw new RuntimeException("지급 완료된 급여는 수정할 수 없습니다.");
        }

        if (salary.getSalaryType() != SalaryType.POSITION) {
            throw new RuntimeException("개인 급여는 수정할 수 없습니다.");
        }

        // 급여월 변경 가능
        if (dto.getSalaryMonth() != null) {
            YearMonth salaryMonth = YearMonth.parse(dto.getSalaryMonth());
            salary.setSalaryMonth(salaryMonth);
            salary.setPayDate(LocalDate.of(salaryMonth.getYear(), salaryMonth.getMonth(), 20));
        }

        // Salary에 PositionSalary 값을 복사하여 수정
        if (dto.getTitle() != null) salary.setTitle(dto.getTitle());
        if (dto.getBaseSalary() != null) salary.setBaseSalary(dto.getBaseSalary());
        if (dto.getHourlyRate() != null) salary.setHourlyRate(dto.getHourlyRate());

        // 총 급여 재계산
        BigDecimal hoursBaseSalary = salary.getHoursBaseSalary() != null ? salary.getHoursBaseSalary() : BigDecimal.ZERO;
        BigDecimal grossPay = salary.getBaseSalary().add(hoursBaseSalary);
        salary.setGrossPay(grossPay);

        // 공제 재계산
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

        // 순급여 재계산
        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(grossPay.subtract(totalDeduction));

        // PositionSalary는 건드리지 않고 Salary만 저장
        Salary saved = salaryRepository.save(salary);
        return convertResponse(saved);
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


    // 급여 승인
    public SalaryResponseDto approve(Integer salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("해당 급여가 존재하지 않습니다."));
        salary.setStatus(SalaryStatus.COMPLETED);
        salary.setPayDate(LocalDate.of(salary.getSalaryMonth().getYear(), salary.getSalaryMonth().getMonth(), 20));
        salaryRepository.save(salary);
        return convertResponse(salary);
    }

    // 전체 급여 조회
    public List<SalaryResponseDto> findAll() {
        return salaryRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getPayDate().compareTo(a.getPayDate())) // 최신순 정렬
                .map(this::convertResponse)
                .collect(Collectors.toList());
    }

    // 미승인 급여 조회
    public List<SalaryResponseDto> findDraftSalaries() {
        return salaryRepository.findByStatusOrderByPayDateDesc(SalaryStatus.DRAFT)
                .stream()
                .map(this::convertResponse)
                .collect(Collectors.toList());
    }

    // 승인된 급여 조회
    public List<SalaryResponseDto> findCompletedSalariesFiltered(String memberId, String salaryMonthStr) {
        Optional<String> optMemberId = Optional.ofNullable(memberId).filter(s -> !s.isBlank());
        Optional<YearMonth> optMonth = Optional.empty();
        if (salaryMonthStr != null && !salaryMonthStr.isBlank()) {
            optMonth = Optional.of(YearMonth.parse(salaryMonthStr));
        }

        List<Salary> salaries;

        if (optMemberId.isPresent() && optMonth.isPresent()) {
            salaries = salaryRepository.findByStatusAndMember_IdAndSalaryMonthOrderByPayDateDesc(
                    SalaryStatus.COMPLETED, optMemberId.get(), optMonth.get()
            );
        } else if (optMemberId.isPresent()) {
            salaries = salaryRepository.findByStatusAndMember_IdOrderByPayDateDesc(
                    SalaryStatus.COMPLETED, optMemberId.get()
            );
        } else if (optMonth.isPresent()) {
            salaries = salaryRepository.findByStatusAndSalaryMonthOrderByPayDateDesc(
                    SalaryStatus.COMPLETED, optMonth.get()
            );
        } else {
            salaries = salaryRepository.findByStatusOrderByPayDateDesc(SalaryStatus.COMPLETED);
        }

        return salaries.stream()
                .map(this::convertResponse)
                .collect(Collectors.toList());
    }

    // 나의 급여 내역
    public List<SalaryResponseDto> findByMemberId(String memberId) {
        return salaryRepository.findByMember_IdAndStatusOrderByPayDateDesc(memberId, SalaryStatus.COMPLETED)
                .stream()
                .map(this::convertResponse)
                .collect(Collectors.toList());
    }

    // 나의 급여 상세
    public SalaryResponseDto findMySalaryDetail(String memberId, Integer salaryId) {
        Salary salary = salaryRepository.findBySalaryIdAndMemberId(salaryId, memberId)
                .orElseThrow(() -> new RuntimeException("해당 급여 내역을 찾을 수 없습니다."));
        return convertResponse(salary);
    }

    // 급여 삭제
    public void delete(Integer salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("삭제할 급여가 존재하지 않습니다."));

        if (salary.getStatus() == SalaryStatus.COMPLETED) {
            throw new RuntimeException("지급 완료된 급여는 삭제할 수 없습니다.");
        }

        salaryRepository.delete(salary);
    }


}
