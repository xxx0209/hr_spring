package com.hr.constant;

// 회원의 유형을 정의하는 열거형 상수
// ROLE_USER(일반 사용자), ROLE_ADMIN(관리자)
public enum MemberRole {

    ROLE_USER("일반유저"), ROLE_ADMIN("관리자");

    private final String label;

    MemberRole(String label) {
        this.label = label;
    }

    public String getValue() {
        return name(); // ROLE_USER
    }

    public String getLabel() {
        return label; // 일반유저
    }
}