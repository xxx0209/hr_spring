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
public class SimplePositionDto extends BaseDto<Position> {

    private Long positionId;
    private String positionName;

    private Long value;
    private String label;

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
        this.value = positionId;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
        this.label = positionName;
    }

    @Override
    protected Class<Position> getEntityClass() {
        return Position.class;
    }
}
