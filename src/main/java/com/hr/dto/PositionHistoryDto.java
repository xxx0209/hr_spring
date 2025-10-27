package com.hr.dto;

import com.hr.entity.Member;
import com.hr.entity.PositionHistory;
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
    private String memberId;
    private Long oldPositionId;
    private Long newPositionId;
    private String changeReason;
    private LocalDateTime changedAt;

    @Override
    protected Class<PositionHistory> getEntityClass() {
        return PositionHistory.class;
    }

}
