package com.dsvn.starterkit.domains.models.user;

import com.dsvn.starterkit.annotation.filedata.FileDataBindByName;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @Email
    @FileDataBindByName("email")
    private String email;

    @FileDataBindByName("password")
    private String password;

    @FileDataBindByName("first_name")
    private String firstName;

    @FileDataBindByName("last_name")
    private String lastName;

    @FileDataBindByName("phone_number")
    private String phoneNumber;

    private Role role;

    private String avatar;

    private String address;

    private String birthday;

    private Integer gender;
}
