package com.hr.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchRequestDto {
    private String searchType; // username, name, position, hireDate
    private String keyword;
    private String hireDate;
    private int page = 0;
    private int size = 10;
}
