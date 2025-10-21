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
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer id;
    @Column(name = "default_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal defaultSalary;
    private String title;
    private String role;
}
