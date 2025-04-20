package net.skhu.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserEdit {
    int id;

    @NotEmpty(message="로그인 아이디를 입력하세요")
    @Size(min=3, max=15)
    String loginName;

    @NotEmpty(message="이름을 입력하세요")
    @Size(min=2, max=30)
    String name;

    @NotEmpty(message="이메일 주소를 입력하세요")
    @Email(message="이메일 주소가 올바르지 않습니다")
    String email;

    boolean enabled;

    @NotEmpty(message="사용자 유형을 선택하세요")
    @Pattern(regexp="관리자|교수|학생")
    String userType;
}
