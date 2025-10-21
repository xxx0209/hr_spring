package com.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "deductiontypes")
public class DeductionType {

    @Id
    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "default_rate" ,precision = 12, scale = 2)
    private BigDecimal defaultRate;

    @OneToMany(mappedBy = "deductionType", cascade = CascadeType.ALL)
    private List<TaxDeduction> taxDeductions;
}
