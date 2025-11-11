package com.hr.constant;

public enum PositionCode {
//    INTERN("인턴"),
//    STAFF("직원"),
//    ASSISTANT("대리"),
//    CEO("사장");

    CEO("대표이사"), VP("부사장"), SED("전무"), ED("상무"), DIR("이사"), GM("부장"), DGM("차장"), MGR("과장"), AM("대리"), SS("주임"), STF("사원"), IN("인턴");

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
