package com.hr.dto.member;

import com.hr.constant.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRoleUpdateDto {

    @NotBlank
    private String memberId;      // 권한 변경할 회원 아이디

    @NotNull
    private MemberRole memberRole; // Enum 타입
}
