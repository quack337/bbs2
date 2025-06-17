package net.skhu.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendPwMail {
    @NotNull @NotEmpty
    String loginName;

    @NotNull @NotEmpty
    @Email
    String email;
}

