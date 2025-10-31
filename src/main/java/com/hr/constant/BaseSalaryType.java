package com.hr.constant;

public enum BaseSalaryType {
    POSITION("직급"), // 직급
    MEMBER("개인"); // 개인

    private final String label;

    BaseSalaryType(String label) {
        this.label = label;
    }

    public String getValue() {
        return name(); // POSITION
    }

    public String getLabel() {
        return label; // 직급
    }
}
