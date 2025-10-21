package com.hr.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "taxDeductions") // 순환 참조 방지
@EqualsAndHashCode(exclude = "taxDeductions")
@Table(name = "salaries")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "pay_date")
    private LocalDate payDate;

    @Column(name = "base_salary", nullable = false,precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "overtime_pay",precision = 12, scale = 2)
    private BigDecimal overtimePay;

    @Column(name = "gross_pay", nullable = false,precision = 12, scale = 2)
    private BigDecimal grossPay;

    @Column(name = "total_deduction", nullable = false,precision = 12, scale = 2)
    private BigDecimal totalDeduction;

    private String status;

    @Column(name = "net_pay", nullable = false ,precision = 12, scale = 2)
    private BigDecimal netPay;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxDeduction> taxDeductions = new ArrayList<>();
}
