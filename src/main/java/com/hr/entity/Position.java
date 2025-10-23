package com.hr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "members")
@Table(name = "positions")
public class Position {

    @Id
    @Column(name = "title")
    private String title; // 직급명 (예: 인턴, 직원, 대리, 사장)

    @OneToMany(mappedBy = "position")
    private List<Member> members;

}
