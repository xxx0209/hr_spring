package com.hr.dto;

import com.hr.entity.Position;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDto extends BaseDto<Position>{

    private Long positionId;

    @NotBlank(message = "직급코드는 필수입니다.")
    private String positionCode; // ENUM: INTERN, STAFF, ASSISTANT, CEO

    @NotBlank(message = "직급명은 필수입니다.")
    private String positionName;       // 한글 표시: 인턴, 직원 등

    @NotBlank(message = "직급설명은 필수입니다.")
    private String description;        // 설명

    private Boolean active = true;

    @Override
    protected Class<Position> getEntityClass() {
        return Position.class;
    }
}
