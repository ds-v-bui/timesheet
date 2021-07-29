package com.dsvn.starterkit.api.controllers;

import com.dsvn.starterkit.api.responses.Response;
import com.dsvn.starterkit.domains.forms.auth.ChangePasswordForm;
import com.dsvn.starterkit.domains.forms.auth.LoginForm;
import com.dsvn.starterkit.domains.forms.auth.ResetPasswordForm;
import com.dsvn.starterkit.infrastructure.security.UserPrincipal;
import com.dsvn.starterkit.services.UserAuthenticationService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private UserAuthenticationService userAuthenticationService;

    @PreAuthorize("@permissionCheck.permitAll()")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginForm loginForm, HttpServletResponse response) {
        UserDetails userDetails = userAuthenticationService.getUserLogin(loginForm);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        String token = userAuthenticationService.createToken(authentication, response);

        return Response.ofResource(token);
    }

    @PreAuthorize("@permissionCheck.isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        userAuthenticationService.removeToken(request, response);
        return Response.ofNoContent();
    }

    @PreAuthorize("@permissionCheck.isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Response.ofResource(userPrincipal);
    }

    @PreAuthorize("@permissionCheck.isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordForm changePasswordForm,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        userAuthenticationService.changePassword(changePasswordForm);
        String token = userAuthenticationService.updateToken(authentication, request, response);

        return Response.ofResource(token);
    }

    @PreAuthorize("@permissionCheck.permitAll()")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordForm resetPasswordForm) {
        userAuthenticationService.resetPassword(resetPasswordForm.getEmail());
        return Response.ofNoContent();
    }
}
