package com.dsvn.starterkit.repositories.custom;

import com.dsvn.starterkit.domains.forms.user.UserSearchForm;
import com.dsvn.starterkit.domains.models.user.UserDTO;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Long save(UserDTO userDTO);

    int[] saveAll(List<UserDTO> userDTOS);

    void update(UserDTO userDTO);

    //    void deleteById(Long id);

    Page<UserDTO> findAll(UserSearchForm userSearchForm, Pageable pageable);

    Optional<UserDTO> findByEmail(String email);

    //    Optional<User> findById(Long id);

    boolean existsByEmail(String email);
}
