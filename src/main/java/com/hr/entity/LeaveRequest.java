package com.hr.entity;

import com.hr.security.CustomUserDetails;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING; //기본값 설정

    public void setStatus(LeaveStatus leaveStatus) {
    }

    public void setUserId(Object userId) {
    }

    public void setLeaveType(Object leaveType) {
    }

    public void setStartDate(Object startDate) {
    }

    public void setEndDate(Object endDate) {
    }

    public void setReason(Object reason) {
    }

    public Object getId() {
        return null;
    }

    public Object getUserId() {
        return null;
    }

    public Object getLeaveType() {
        return null;
    }

    public Object getStartDate() {
        return null;
    }

    public Object getEndDate() {
        return null;
    }

    public Object getReason() {
        return null;
    }

    public CustomUserDetails.Builder getStatus() {
        return null;
    }
}