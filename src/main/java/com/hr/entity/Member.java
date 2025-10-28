package com.hr.entity;

import com.hr.constant.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter @Setter @ToString
public class Member extends BaseEntity {

    @Id // 이 컬럼은 primary key입니다.
    @Column(name = "member_id", length = 50)
    private String id; //PK (자동 증가 X)
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String email;
    @Column(length = 20)
    private String gender;
    @Column(length = 8)
    private String hiredate;
    @Column(length = 500)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position; // FK → positions.position_id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MemberRole memberRole;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<PositionHistory> histories = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (memberRole == null) {
            memberRole = MemberRole.ROLE_USER;
        }
    }

}
