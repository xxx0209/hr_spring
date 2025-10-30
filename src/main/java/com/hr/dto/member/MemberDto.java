package com.hr.dto.member;

import com.hr.dto.BaseDto;
import com.hr.entity.Member;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto extends BaseDto<Member> {
    private Long id;
    private String memberId;
    private String name;
    private String role;

    @Override
    protected Class<Member> getEntityClass() {
        return Member.class;
    }
}




