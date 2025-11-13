package com.hr.dto.member;

import com.hr.dto.BaseDto;
import com.hr.entity.PositionHistory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionHistoryDto extends BaseDto<PositionHistory> {

    private Long id;
    @NotBlank(message = "직원 선택은 필수입니다.")
    private String memberId;
    private String memberName;
    private Long oldPositionId;
    private String oldPositionName;
    @NotNull(message = "새 직급선택은 필수입니다.")
    private Long newPositionId;
    private String newPositionName;
    @NotBlank(message = "변경사유는 필수입니다.")
    private String changeReason;
    private LocalDateTime changedAt;

    @Override
    protected Class<PositionHistory> getEntityClass() {
        return PositionHistory.class;
    }

}
