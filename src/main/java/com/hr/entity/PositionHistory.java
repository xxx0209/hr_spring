package com.hr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "position_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionHistory extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: Member (member_id가 PK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private Member member;

    // 변경 전 직급
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_position_id")
    private Position oldPosition;

    // 변경 후 직급
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_position_id", nullable = false)
    private Position newPosition;

    @Column(length = 255)
    private String changeReason;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
