package com.dsvn.starterkit.services;

import com.dsvn.starterkit.domains.forms.user.UserCreateForm;
import com.dsvn.starterkit.domains.forms.user.UserSearchForm;
import com.dsvn.starterkit.domains.forms.user.UserUpdateForm;
import com.dsvn.starterkit.domains.models.DataUri;
import com.dsvn.starterkit.domains.models.user.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDTO loadUserById(Long id);

    Page<UserDTO> findAll(UserSearchForm userSearchForm, Pageable pageable);

    Long create(UserCreateForm userCreateForm);

    void update(UserUpdateForm userUpdateForm);

    void update(Long userId, UserUpdateForm userUpdateForm);

    void delete();

    void delete(Long userId);

    int[] multiCreateByFile(DataUri base64);
}
