package com.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@Table(name = "tax_deductions")
public class TaxDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deductiontype_id")
    private Integer Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code", nullable = false)
    private DeductionType deductionType;

    @Column(name = "amount", nullable = false,precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "rate", precision = 5, scale = 4)
    private BigDecimal rate;

}

