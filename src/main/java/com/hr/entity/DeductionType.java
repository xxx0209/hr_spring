package com.hr.entity;

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
@AllArgsConstructor
@Table(name = "deduction_types")
public class DeductionType {

    @Id
    @Column(name = "type_code")
    private String typeCode; // 예: NP, HI, EI, IT

    private String name;

    @Column(name = "default_rate", precision = 5, scale = 4)
    private BigDecimal defaultRate;
}


