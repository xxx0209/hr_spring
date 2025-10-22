// 📁 entity/Request.java
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;   // 작성자 (기안자)

    @Column(name = "request_type", length = 100)
    private String requestType; // 연차, 반차, 휴가 등

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(length = 100)
    private String status; // 진행 상태(작성중, 승인대기, 승인, 반려 등)
}
