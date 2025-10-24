package com.hr.entity;

import com.hr.constant.BaseSalaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "base_salaries")
public class BaseSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type") // "POSITION" 또는 "MEMBER"
    private BaseSalaryType type;

    @Column(name = "reference_id") // 직급명 또는 memberId
    private String referenceId;

    @Column(name = "base_salary", precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "hourly_rate", precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    public BaseSalary(BaseSalaryType type, String referenceId, BigDecimal baseSalary, BigDecimal hourlyRate) {
        this.type = type;
        this.referenceId = referenceId;
        this.baseSalary = baseSalary;
        this.hourlyRate = hourlyRate;
    }

}

