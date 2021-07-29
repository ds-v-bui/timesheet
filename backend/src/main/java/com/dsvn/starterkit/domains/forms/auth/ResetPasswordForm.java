package com.dsvn.starterkit.domains.forms.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordForm {
    @NotBlank @Email String email;
}
