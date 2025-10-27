package com.hr.dto;

import com.hr.entity.Member;
import com.hr.entity.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SimpleMemberDto extends BaseDto<Member>{

    private String id;
    private String name;
    private PositionDto position;

    private String value;
    private String label;
    private String memberId;

    public void setId(String id) {
        this.id = id;
        this.memberId = id;
        this.value = id;
    }

    public void setName(String name) {
        this.name = name;
        this.label = name;
    }

    @Override
    protected Class<Member> getEntityClass() {
        return Member.class;
    }

}
