package com.hr.dto.member;

import com.hr.dto.BaseDto;
import com.hr.entity.BaseEntity;
import com.hr.entity.Category;
import com.hr.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto extends BaseDto<Category> {

    private Long categoryId;

    @NotBlank(message = "카테고리명은 필수 입력사항입니다.")
    private String name;

    @NotBlank(message = "색상은 필수 선택 사항입니다.")
    private String color;

    private Boolean active = true;

    @Override
    protected Class<Category> getEntityClass() {
        return Category.class;
    }
}
