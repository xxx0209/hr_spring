package com.hr.constant;

public enum SalaryType {
    POSITION("직급"), // 직급
    MEMBER("개인"); // 개인

    private final String label;

    SalaryType(String label) {
        this.label = label;
    }

    public String getValue() {
        return name(); // POSITION
    }

    public String getLabel() {
        return label; // 직급
    }
}
