package net.skhu.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSignUp {

    @NotEmpty(message="로그인 아이디를 입력하세요")
    @Size(min=3, max=15)
    String loginName;

    @NotEmpty(message="비밀번호를 입력하세요")
    @Size(min=6, max=12, message="6 자리 이상 12 자리 이하이어야 합니다.")
    String passwd1;

    @NotEmpty(message="비밀번호를 한번 더 입력하세요")
    String passwd2;

    @NotEmpty(message="이름을 입력하세요")
    @Size(min=2, max=30)
    String name;

    @NotEmpty(message="이메일 주소를 입력하세요")
    @Email(message="이메일 주소가 올바르지 않습니다")
    String email;

    @NotEmpty(message="사용자 유형을 선택하세요")
    @Pattern(regexp="교수|학생")
    String userType;
}
