package com.hr.entity;

import com.hr.dto.MemberDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

@Entity
@Table(name = "members")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

    @Id // 이 컬럼은 primary key입니다.
    @Column(name = "member_id") // 컬럼 이름을 변경합니다.
    private String id;
    private String name;
    private String password;
    private String email;
    private String gender;
    private String hiredate;
    private String address;

    @Column(name = "custom_base_salary", precision = 12, scale = 2)
    private BigDecimal customBaseSalary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;
}
