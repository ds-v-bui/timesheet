package com.dsvn.starterkit.domains.forms.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginForm {
    @NotBlank @Email String email;

    @NotBlank String password;
}
