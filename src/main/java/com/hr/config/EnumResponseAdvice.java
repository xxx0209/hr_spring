package com.hr.config;

import com.hr.util.EnumMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice // 모든 @RestController 응답에 적용됨
public class EnumResponseAdvice implements ResponseBodyAdvice<Object> {

    // 어떤 응답에 적용할지 설정 (여기선 전부 true → 전체 컨트롤러 확인)
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    // 실제 응답 바디(JSON)가 쓰이기 직전에 호출됨
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // 1. 메서드에 @IncludeEnums 어노테이션이 붙어있는지 확인
        IncludeEnums annotation = returnType.getMethodAnnotation(IncludeEnums.class);
        if (annotation == null) {
            return body; // 어노테이션 없으면 그대로 응답 리턴
        }

        // 2. @IncludeEnums에 지정된 Enum 클래스 배열 가져오기
        Class<? extends Enum<?>>[] enumClasses = annotation.value();

        // 3. 각 Enum을 Map 구조로 변환
        Map<String, Object> enumsMap = new LinkedHashMap<>();
        for (Class<? extends Enum<?>> enumClass : enumClasses) {
            enumsMap.put(enumClass.getSimpleName(), EnumMapper.toList(enumClass));
        }

        // 4️. 응답 JSON 구조 통합
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("data", body);       // 원래 컨트롤러 리턴값
        responseMap.put("enums", enumsMap);  // 추가된 Enum 리스트들

        // 5️. 최종 JSON 반환
        return responseMap;
    }
}
