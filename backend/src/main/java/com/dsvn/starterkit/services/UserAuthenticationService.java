package com.dsvn.starterkit.services;

import com.dsvn.starterkit.domains.forms.auth.ChangePasswordForm;
import com.dsvn.starterkit.domains.forms.auth.LoginForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserAuthenticationService {
    UserDetails getUserLogin(LoginForm loginForm);

    UserDetails loadUserDetailsById(Long id);

    void changePassword(ChangePasswordForm changePasswordForm);

    String createToken(Authentication authentication, HttpServletResponse response);

    void removeToken(HttpServletRequest request, HttpServletResponse response);

    String updateToken(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response);

    void resetPassword(String email);
}
