package com.hr.constant.common;

import com.hr.constant.EnumMapperType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumMapper {
    /**
     * Enum 클래스를 Map 리스트로 변환
     * value = Enum.name()
     * label = Enum.getLabel() 있으면 사용, 없으면 name()
     */
    public static List<Map<String, String>> toList(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> {
                    String label;
                    try {
                        // getLabel 메서드가 있으면 호출
                        label = (String) enumClass.getMethod("getLabel").invoke(e);
                    } catch (Exception ex) {
                        // 없으면 name() 사용
                        label = ((Enum<?>) e).name();
                    }
                    return Map.of(
                            "value", ((Enum<?>) e).name(),
                            "label", label
                    );
                })
                .collect(Collectors.toList());
    }
}
