// 📁 dto/ApprovalDto.java
package com.hr.dto;

import com.hr.entity.Approval;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprovalDto extends BaseDto {

    private Long id;
    private Long requestId;
    private String approverId;
    private String status;
    private String comment;
    private LocalDateTime updatedAt;
    private Integer stepOrder;
    private Boolean isFinal;

    public Approval toEntity() {
        return modelMapper.map(this, Approval.class);
    }

    public static ApprovalDto of(Approval entity) {
        return modelMapper.map(entity, ApprovalDto.class);
    }
}
