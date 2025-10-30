package com.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
public class Request extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "member_id", length = 100)
    private String memberId;

    @Column(name = "member_name", length = 100)
    private String memberName;

    @Column(name = "request_type", length = 100)
    private String requestType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "price")
    private Integer price;

    @Column(length = 100)
    private String status;

    // 결재자 정보 및 결재일시
    @Column(name = "approver_name", length = 100)
    private String approver;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "comment", columnDefinition = "Text")
    private String comment;
}
