package com.dsvn.starterkit.domains.forms.auth;

import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ChangePasswordForm {

    @NotBlank String currentPassword;

    @NotBlank String newPassword;
}
