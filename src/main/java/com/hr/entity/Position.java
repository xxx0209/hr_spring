package com.hr.entity;

import com.hr.constant.PositionCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString
@Table(name = "positions")
public class Position extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionId;

    @Column(nullable = false, unique = true, length = 50)
    private String positionCode;  // INTERN, STAFF, ASSISTANT, CEO

    @Column(nullable = false, length = 50)
    private String positionName; // 인턴, 직원, 대리, 사장

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

}
