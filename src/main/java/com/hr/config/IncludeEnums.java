package com.hr.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD})      //  메서드 위에만 적용
@Retention(RetentionPolicy.RUNTIME) //  런타임까지 유지되어 리플렉션으로 읽을 수 있음
@Documented
public @interface IncludeEnums {

    //  value()는 여러 Enum 클래스를 받을 수 있도록 배열 형태로 선언
    Class<? extends Enum<?>>[] value();
}
