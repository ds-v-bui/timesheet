package com.dsvn.starterkit.api.controllers;

import com.dsvn.starterkit.api.responses.Response;
import com.dsvn.starterkit.domains.forms.Base64Form;
import com.dsvn.starterkit.domains.forms.user.UserCreateForm;
import com.dsvn.starterkit.domains.forms.user.UserSearchForm;
import com.dsvn.starterkit.domains.forms.user.UserUpdateForm;
import com.dsvn.starterkit.domains.models.DataUri;
import com.dsvn.starterkit.domains.models.user.UserDTO;
import com.dsvn.starterkit.helpers.exporter.CsvExporter;
import com.dsvn.starterkit.helpers.exporter.ExcelExporter;
import com.dsvn.starterkit.services.UserService;
import java.io.InputStream;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private UserService userService;

    @PreAuthorize("@permissionCheck.permitAll()")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateForm userCreateForm) {
        Long userId = userService.create(userCreateForm);
        return Response.ofCreated(userId);
    }

    @PreAuthorize("@permissionCheck.isAuthenticated()")
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateForm userUpdateForm) {
        userService.update(userUpdateForm);
        return Response.ofNoContent();
    }

    @PreAuthorize("@permissionCheck.allowAdmin()")
    @GetMapping("/search")
    public ResponseEntity<?> searchUser(UserSearchForm userSearchForm, Pageable pageable) {
        Page<UserDTO> users = userService.findAll(userSearchForm, pageable);
        return Response.ofResource(users);
    }

    @PreAuthorize("@permissionCheck.allowAdmin()")
    @PostMapping("/upload-create")
    public ResponseEntity<?> uploadCreate(@Valid @RequestBody Base64Form base64Form) {
        DataUri dataUri = new DataUri(base64Form.getDataUri());
        int[] insertRows = userService.multiCreateByFile(dataUri);
        return Response.ofResource(insertRows);
    }

    @PreAuthorize("@permissionCheck.allowAdmin()")
    @GetMapping("/export-excel")
    public ResponseEntity<?> exportExcel(UserSearchForm userSearchForm, Pageable pageable) {
        Page<UserDTO> users = userService.findAll(userSearchForm, pageable);

        InputStream in = new ExcelExporter<UserDTO>().download(users.getContent());

        return Response.ofDownloadFile(in, "users.xlsx");
    }

    @PreAuthorize("@permissionCheck.allowAdmin()")
    @GetMapping("/export-csv")
    public ResponseEntity<?> exportCSV(UserSearchForm userSearchForm, Pageable pageable) {
        Page<UserDTO> users = userService.findAll(userSearchForm, pageable);

        InputStream in = new CsvExporter<UserDTO>().download(users.getContent());

        return Response.ofDownloadFile(in, "users.csv");
    }
}
