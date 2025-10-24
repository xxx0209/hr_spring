package com.hr.dto;

import com.hr.constant.MemberRole;
import com.hr.entity.Member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto extends BaseDto {

    @NotBlank(message = "아이디는 필수 입력 사항입니다.")
    private String memberId;

    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;

    @NotBlank(message = "비밀 번호는 필수 입력 사항입니다.")
    @Size(min = 8, max = 255, message = "비밀 번호는 8자리 이상, 255자리 이하로 입력해 주세요.")
    @Pattern(regexp = ".*[A-Z].*", message = "비밀 번호는 대문자 1개 이상을 포함해야 합니다.")
    @Pattern(regexp = ".*[!@#$%].*", message = "비밀 번호는 특수 문자 '!@#$%' 중 하나 이상을 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "올바른 이메일 형식으로 입력해 주셔야 합니다.")
    private String email;

    @NotBlank(message = "성별은 필수 입력 사항입니다.")
    private String gender;

    @NotBlank(message = "주소는 필수 입력 사항입니다.")
    private String address;

    private MemberRole memberRole;

    public Member careteMember() {
        return modelMapper.map(this, Member.class);
    }

    public static MemberDto of(Member member) {
        return modelMapper.map(member, MemberDto.class);
    }
}
