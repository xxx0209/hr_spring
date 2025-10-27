package com.hr.util;

import java.util.*;
import java.util.stream.Collectors;

public class EnumMapper {

    // Enum 클래스 → [{value:"ROLE_USER", label:"일반유저"}, ...] 형태로 변환
    public static List<Map<String, String>> toList(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()) // Enum 상수 목록 가져오기
                .map(e -> Map.of(
                        "value", e.name(),       // Enum의 이름 (예: ROLE_USER)
                        "label", getLabel(e)     // Enum의 label 필드값 (예: 일반유저)
                ))
                .collect(Collectors.toList());
    }

    // Enum 안에 "label"이라는 필드를 찾아서 가져오는 헬퍼 메서드
    private static String getLabel(Enum<?> e) {
        try {
            // Enum 클래스의 "label" 필드 접근
            var field = e.getDeclaringClass().getDeclaredField("label");
            field.setAccessible(true);
            // 해당 Enum 인스턴스의 label 필드값 반환
            return String.valueOf(field.get(e));
        } catch (Exception ex) {
            // label 필드가 없으면 Enum 이름으로 fallback
            return e.name();
        }
    }
}
