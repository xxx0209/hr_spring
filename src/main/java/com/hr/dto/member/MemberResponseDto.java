package com.hr.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private String memberId;
    private String name;
    private String positionName;
    private String gender;
    private String email;
    private String memberRole;
    private String memberRoleLabel;
    private String hiredate;
    private String profileImage;
}
