package com.dsvn.starterkit.domains.forms.user;

import lombok.Value;

@Value
public class UserSearchForm {
    String email;

    String firstName;

    String lastName;

    String phoneNumber;

    String address;

    String birthday;

    Integer gender;
}
