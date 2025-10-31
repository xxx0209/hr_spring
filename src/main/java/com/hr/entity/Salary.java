package com.hr.entity;

import com.hr.constant.SalaryStatus;
import com.hr.service.YearMonthAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "salaries")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Integer salaryId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "salary_month")
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth salaryMonth;


    @Column(name = "pay_date")
    private LocalDate payDate;

    @Column(name = "custom_base_salary", precision = 12, scale = 2)
    private BigDecimal customBaseSalary; // 개인 기준 급여 (null 가능)

    @Column(name = "hours_base_salary", precision = 12, scale = 2)
    private BigDecimal hoursBaseSalary; // 시급 × 1.5 × 시간

    @Column(name = "gross_pay", precision = 12, scale = 2)
    private BigDecimal grossPay;

    @Column(name = "total_deduction", precision = 12, scale = 2)
    private BigDecimal totalDeduction;

    @Column(name = "net_pay", precision = 12, scale = 2)
    private BigDecimal netPay;

    @Enumerated(EnumType.STRING)
    private SalaryStatus status;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxDeduction> taxDeductions = new ArrayList<>();;


}
