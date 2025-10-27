package com.hr.controller;

import com.hr.util.EnumMapper;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/all")
    public Map<String, List<Map<String, String>>> getAllEnums() {
        Map<String, List<Map<String, String>>> result = new HashMap<>();

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Enum.class));

        String packageToScan = "com.hr.constant";

        // 2️⃣ 패키지 내 Enum 클래스 조회
        scanner.findCandidateComponents(packageToScan).forEach(beanDef -> {
            try {
                Class<?> clazz = Class.forName(beanDef.getBeanClassName());
                if (clazz.isEnum()) {
                    // 3️⃣ EnumMapper로 변환
                    result.put(clazz.getSimpleName(), EnumMapper.toList((Class<? extends Enum<?>>) clazz));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        return result;
    }
}
