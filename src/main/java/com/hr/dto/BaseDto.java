package com.hr.dto;

import org.modelmapper.ModelMapper;

public abstract class BaseDto<T> {

    private static final ModelMapper modelMapper = new ModelMapper();

    /** DTO -> Entity */
    public T toEntity() {
        return modelMapper.map(this, getEntityClass());
    }

    /** Entity -> DTO */
    public static <T, D extends BaseDto<T>> D fromEntity(T entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    /** 각 DTO에서 매핑할 Entity 클래스 지정 */
    protected abstract Class<T> getEntityClass();
}
