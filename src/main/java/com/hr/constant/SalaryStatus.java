package com.hr.constant;

public enum SalaryStatus {
    DRAFT("승인 대기중"),       // 생성됨, 승인 전
    REJECTED("승인 거절"),    // 승인 거절
    CANCELLED("취소됨"),   // 취소됨
    COMPLETED("지급 완료");    // 지급 완료

    private final String label;

    SalaryStatus(String label) {
        this.label = label;
    }

    public String getValue() {
        return name(); // DRAFT
    }

    public String getLabel() {
        return label; // 인턴
    }
}
