package com.hr.dto;

import com.hr.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class MemberUpdateDto extends BaseDto<Member>{

    private String id;

    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;

    @Size(min = 8, max = 255, message = "비밀 번호는 8자리 이상, 255자리 이하로 입력해 주세요.")
    @Pattern(regexp = ".*[A-Z].*", message = "비밀 번호는 대문자 1개 이상을 포함해야 합니다.")
    @Pattern(regexp = ".*[!@#$%].*", message = "비밀 번호는 특수 문자 '!@#$%' 중 하나 이상을 포함해야 합니다.")
    private String password; // 빈 값이면 변경하지 않음

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "올바른 이메일 형식으로 입력해 주셔야 합니다.")
    private String email;

    @NotBlank(message = "성별은 필수 입력 사항입니다.")
    private String gender;

    private String hiredate;

    @NotBlank(message = "주소는 필수 입력 사항입니다.")
    private String address;
    private String positionName;
    private MultipartFile profileImage;

    // 서버 저장된 이미지 접근 URL
    private String profileImageUrl;

    public void setHiredate(String hiredate) {
        if (hiredate != null && hiredate.contains("-")) {
            this.hiredate = hiredate.replace("-", "");
        } else {
            this.hiredate = hiredate;
        }
    }

    @Override
    protected Class<Member> getEntityClass() {
        return Member.class;
    }
}
