// 📁 entity/Approval.java
package com.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
@Getter
@Setter
@ToString
public class Approval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member approver; // 승인자

    @Column(name = "status", length = 100)
    private String status; // 승인, 반려

    @Column(name = "comment", length = 255)
    private String comment;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "step_order")
    private Integer stepOrder; // 결재 순서

    @Column(name = "is_final")
    private Boolean isFinal; // 최종결재 여부
}
