package com.hr.service;

import com.hr.constant.SalaryStatus;
import com.hr.constant.SalaryType;
import com.hr.dto.SalaryResponseDto;
import com.hr.dto.TaxDeductionDetailDto;
import com.hr.entity.*;
import com.hr.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
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

        // 2️⃣ 동일한 급여 월에 대해 이미 급여가 존재하는지 확인
        YearMonth salaryMonth = dto.getSalaryMonth() != null
                ? YearMonth.parse(dto.getSalaryMonth())
                : YearMonth.now();

        // 동일한 급여 월에 급여가 이미 존재하는지 확인
        boolean exists = salaryRepository.existsByMemberAndSalaryMonth(member, salaryMonth);
        if (exists) {
            throw new RuntimeException("해당 급여 월에 이미 급여가 존재합니다.");
        }

        Salary salary = new Salary();
        salary.setMember(member);

        //  급여 지급일 20일로고정
        salary.setSalaryMonth(salaryMonth);
        salary.setPayDate(LocalDate.of(salaryMonth.getYear(), salaryMonth.getMonth(), 20));
        salary.setStatus(SalaryStatus.DRAFT);

        // 3️⃣ 급여 기준 조회 (개인 급여 or 직급 급여)
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
            PositionSalary ps = positionSalaries.get(0); // 기본 첫 번째 직급급여 선택
            salary.setSalaryType(SalaryType.POSITION);
            salary.setPositionSalary(ps);
            baseSalary = ps.getBaseSalary() != null ? ps.getBaseSalary() : BigDecimal.ZERO;
            hourlyRate = ps.getHourlyRate() != null ? ps.getHourlyRate() : BigDecimal.ZERO;
        }

        // 4️⃣ 시급/추가급여 처리
        BigDecimal hoursBaseSalary = dto.getHoursBaseSalary() != null ? dto.getHoursBaseSalary() : BigDecimal.ZERO;
        salary.setHoursBaseSalary(hoursBaseSalary);

        // 5️⃣ 총 급여 계산
        BigDecimal grossPay = baseSalary.add(hoursBaseSalary);
        salary.setGrossPay(grossPay);

        // ======================
        // 6️⃣ 공제 계산
        // ======================
        List<DeductionType> deductionTypes = deductionTypeRepository.findAll();
        BigDecimal totalDeduction = BigDecimal.ZERO;

        for (DeductionType type : deductionTypes) {
            BigDecimal rate = type.getDefaultRate() != null ? type.getDefaultRate() : BigDecimal.ZERO;
            BigDecimal amount = grossPay.multiply(rate).setScale(0, BigDecimal.ROUND_HALF_UP);

            TaxDeduction td = new TaxDeduction();
            td.setSalary(salary); // 양방향 연관관계
            td.setDeductionType(type);
            td.setRate(rate);
            td.setAmount(amount);

            salary.getTaxDeductions().add(td); // ← 반드시 추가
            totalDeduction = totalDeduction.add(amount);
        }

        // 총 공제 반영 후 순급여 계산
        salary.setTotalDeduction(totalDeduction);
        salary.setNetPay(grossPay.subtract(totalDeduction));

        // 8️⃣ DB 저장 (한 번만)
        Salary saved = salaryRepository.save(salary);

        return convertResponse(saved);
    }

    // 급여 수정
    public SalaryResponseDto update(Integer salaryId, SalaryResponseDto dto) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("급여가 존재하지 않습니다."));
        if (salary.getStatus() == SalaryStatus.COMPLETED) {
            throw new RuntimeException("지급 완료된 급여는 수정할 수 없습니다.");
        }

        // 직급 급여만 수정 가능
        if (salary.getSalaryType() != SalaryType.POSITION) {
            throw new RuntimeException("개인 급여는 수정할 수 없습니다.");
        }

        // 급여월 변경
        if (dto.getSalaryMonth() != null) {
            YearMonth salaryMonth = YearMonth.parse(dto.getSalaryMonth());
            salary.setSalaryMonth(salaryMonth);
            salary.setPayDate(LocalDate.of(salaryMonth.getYear(), salaryMonth.getMonth(), 20));
        }

        Salary saved = salaryRepository.save(salary);
        return convertResponse(saved);
    }


    //급여 승인

    public SalaryResponseDto approve(Integer salaryId) {
        Salary salary = salaryRepository.findById(salaryId).orElseThrow(() -> new RuntimeException("해당 급여가 존재하지 않습니다."));
        salary.setStatus(SalaryStatus.COMPLETED);
        salary.setPayDate(LocalDate.of(salary.getSalaryMonth().getYear(), salary.getSalaryMonth().getMonth(), 20));
        salaryRepository.save(salary);
        return convertResponse(salary);
    }

    //전체 급여조회

    public List<SalaryResponseDto> findAll() {
        return salaryRepository.findAll().stream().map(this::convertResponse).collect(Collectors.toList());
    }

    /**
     * 미승인 급여 조회(DRAFT)
     */
    public List<SalaryResponseDto> findDraftSalaries() {
        return salaryRepository.findByStatus(SalaryStatus.DRAFT).stream().map(this::convertResponse).collect(Collectors.toList());
    }

    //승인한 급여조회

    public List<SalaryResponseDto> findCompletedSalaries() {
        return salaryRepository.findByStatus(SalaryStatus.COMPLETED).stream().map(this::convertResponse).collect(Collectors.toList());
    }

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

        if (s.getMemberSalary() != null) {
            // 개인 기본급이 있으면 직급 기본급을 무시
            dto.setBaseSalary(s.getMemberSalary().getBaseSalary());
            dto.setHourlyRate(s.getMemberSalary().getHourlyRate());
        } else if (s.getPositionSalary() != null && s.getPositionSalary().getPosition() != null) {
            dto.setPositionId(s.getPositionSalary().getPosition().getPositionId());
            dto.setBaseSalary(s.getPositionSalary().getBaseSalary());
            dto.setHourlyRate(s.getPositionSalary().getHourlyRate());
        }

        // ===========================
        // 공제 내역 변환
        // ===========================
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

    /**
     * 급여 삭제
     */
    public void delete(Integer salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("삭제할 급여가 존재하지 않습니다."));

        if (salary.getStatus() == SalaryStatus.COMPLETED) {
            throw new RuntimeException("지급 완료된 급여는 삭제할 수 없습니다.");
        }

        salaryRepository.delete(salary);
    }

    //  나의 급여내역
    public List<SalaryResponseDto> findByMemberId(String memberId) {
        return salaryRepository.findByMemberIdAndStatus(memberId, "COMPLETED")
                .stream()
                .map(this::convertResponse)
                .collect(Collectors.toList());
    }

    public SalaryResponseDto findMySalaryDetail(String memberId, Integer salaryId) {
        // 급여 내역을 조회
        Salary salary = salaryRepository.findBySalaryIdAndMemberId(salaryId, memberId)
                .orElseThrow(() -> new RuntimeException("해당 급여 내역을 찾을 수 없습니다."));

        // 급여 상세 DTO로 변환
        SalaryResponseDto dto = convertResponse(salary);

        // 공제 내역이 있을 경우, DTO로 변환하여 추가
        if (salary.getTaxDeductions() != null && !salary.getTaxDeductions().isEmpty()) {
            List<TaxDeductionDetailDto> deductionDtos = salary.getTaxDeductions().stream()
                    .map(td -> {
                        TaxDeductionDetailDto detail = new TaxDeductionDetailDto();
                        detail.setTypeCode(td.getDeductionType().getTypeCode());
                        detail.setTypeName(td.getDeductionType().getName());
                        detail.setRate(td.getRate());
                        detail.setAmount(td.getAmount());
                        return detail;
                    })
                    .collect(Collectors.toList());
            dto.setDeductions(deductionDtos);  // 공제 내역을 DTO에 설정
        }

        return dto;  // 완성된 급여 상세 DTO 반환
    }
}