package com.dsvn.starterkit.services.impl;

import com.dsvn.starterkit.domains.entities.User;
import com.dsvn.starterkit.domains.forms.user.UserCreateForm;
import com.dsvn.starterkit.domains.forms.user.UserSearchForm;
import com.dsvn.starterkit.domains.forms.user.UserUpdateForm;
import com.dsvn.starterkit.domains.models.DataUri;
import com.dsvn.starterkit.domains.models.filedata.RowData;
import com.dsvn.starterkit.domains.models.user.Role;
import com.dsvn.starterkit.domains.models.user.UserDTO;
import com.dsvn.starterkit.exceptions.ResourceAlreadyExistException;
import com.dsvn.starterkit.exceptions.ResourceNotFoundException;
import com.dsvn.starterkit.helpers.AuthenticationHelper;
import com.dsvn.starterkit.helpers.importer.Reader;
import com.dsvn.starterkit.helpers.importer.ReaderFactory;
import com.dsvn.starterkit.repositories.UserRepository;
import com.dsvn.starterkit.services.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private UserRepository userRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserDTO loadUserById(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User id " + id + " not found"));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public Page<UserDTO> findAll(UserSearchForm userSearchForm, Pageable pageable) {
        return userRepository.findAll(userSearchForm, pageable);
    }

    @Override
    public Long create(UserCreateForm userCreateForm) {
        UserDTO userDTO = modelMapper.map(userCreateForm, UserDTO.class);

        Optional<UserDTO> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        if (optionalUser.isPresent()) {
            throw new ResourceAlreadyExistException(userDTO.getEmail() + " already exist");
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userDTO.setRole(Role.user);

        return userRepository.save(userDTO);
    }

    @Override
    public void update(UserUpdateForm userUpdateForm) {
        Long userId = AuthenticationHelper.currentUserId();
        update(userId, userUpdateForm);
    }

    @Override
    public void update(Long userId, UserUpdateForm userUpdateForm) {
        UserDTO userDTO = modelMapper.map(userUpdateForm, UserDTO.class);
        userDTO.setId(userId);
        userRepository.update(userDTO);
    }

    @Override
    public void delete() {
        Long userId = AuthenticationHelper.currentUserId();
        delete(userId);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public int[] multiCreateByFile(DataUri base64) {
        Reader<UserDTO> reader = new ReaderFactory<>(UserDTO.class).getReader(base64.getMimeType());

        List<RowData<UserDTO>> rowData = reader.exec(base64.getBase64());

        List<UserDTO> userDTOS =
                rowData.stream()
                        .map(
                                r -> {
                                    UserDTO u = r.getData();
                                    u.setPassword(passwordEncoder.encode(u.getPassword()));
                                    u.setRole(Role.user);
                                    return u;
                                })
                        .collect(Collectors.toList());

        return userRepository.saveAll(userDTOS);
    }
}
