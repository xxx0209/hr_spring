package com.hr.constant;

public enum PositionCode {
    INTERN("인턴"),
    STAFF("직원"),
    ASSISTANT("대리"),
    CEO("사장");

    private final String label;

    PositionCode(String label) {
        this.label = label;
    }

    public String getValue() {
        return name(); // INTERN
    }

    public String getLabel() {
        return label; // 인턴
    }
}
