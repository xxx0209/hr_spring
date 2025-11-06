package com.hr.entity;

import com.hr.constant.SalaryStatus;
import com.hr.constant.SalaryType;
import com.hr.service.YearMonthAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salaries")
@Getter @Setter @NoArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Integer salaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "salary_month", nullable = false)
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth salaryMonth;

    @Column(name = "pay_date")
    private LocalDate payDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type", nullable = false)
    private SalaryType salaryType; // POSITION 또는 MEMBER

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_salary_id", nullable = true)
    private MemberSalary memberSalary; // 개인 기준 급여

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_salary_id", nullable = true)
    private PositionSalary positionSalary; // 직급 기준 급여

    @Column(name = "hours_base_salary", precision = 12, scale = 2)
    private BigDecimal hoursBaseSalary; // 시급 × 1.5 × 시간

    @Column(name = "gross_pay", precision = 12, scale = 2)
    private BigDecimal grossPay;

    @Column(name = "total_deduction", precision = 12, scale = 2)
    private BigDecimal totalDeduction;

    @Column(name = "net_pay", precision = 12, scale = 2)
    private BigDecimal netPay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SalaryStatus status;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxDeduction> taxDeductions = new ArrayList<>();
}
