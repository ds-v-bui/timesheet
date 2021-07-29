package com.dsvn.starterkit.domains.forms.user;

import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UserUpdateForm {
    @NotBlank String firstName;

    @NotBlank String lastName;

    String phoneNumber;

    String avatar;

    String address;

    String birthday;

    Integer gender;
}
