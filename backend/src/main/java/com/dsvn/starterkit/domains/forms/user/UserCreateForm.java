package com.dsvn.starterkit.domains.forms.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UserCreateForm {
    @NotBlank @Email String email;

    @NotBlank String password;

    @NotBlank String firstName;

    @NotBlank String lastName;

    @NotBlank String phoneNumber;

    String avatar;

    String address;

    String birthday;

    Integer gender;
}
