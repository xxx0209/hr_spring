package com.hr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "position_salaries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PositionSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(name = "base_salary", precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "hourly_rate", precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    private Boolean active = true;
}
