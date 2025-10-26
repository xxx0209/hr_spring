package com.hr.constant;

public enum Gender {
    MALE("남"), FEMALE("여");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getValue() {
        return name(); // MALE
    }

    public String getLabel() {
        return label; // 남
    }

}
