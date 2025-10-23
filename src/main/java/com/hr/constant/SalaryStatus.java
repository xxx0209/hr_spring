package com.hr.constant;

public enum SalaryStatus {
    DRAFT,       // 생성됨, 승인 전
    APPROVED,    // 승인 완료
    REJECTED,    // 승인 거절
    CANCELLED,   // 취소됨
    COMPLETED    // 지급 완료
}
