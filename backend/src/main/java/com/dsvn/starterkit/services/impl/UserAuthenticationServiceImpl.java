package com.dsvn.starterkit.services.impl;

import com.dsvn.starterkit.domains.entities.PasswordResetToken;
import com.dsvn.starterkit.domains.entities.User;
import com.dsvn.starterkit.domains.forms.auth.ChangePasswordForm;
import com.dsvn.starterkit.domains.forms.auth.LoginForm;
import com.dsvn.starterkit.domains.models.mail.Mail;
import com.dsvn.starterkit.domains.models.mail.MailTemplate;
import com.dsvn.starterkit.domains.models.user.UserDTO;
import com.dsvn.starterkit.exceptions.PasswordNotMatchesException;
import com.dsvn.starterkit.helpers.AuthenticationHelper;
import com.dsvn.starterkit.helpers.UrlGenerator;
import com.dsvn.starterkit.infrastructure.configuration.AppProperties;
import com.dsvn.starterkit.infrastructure.security.UserPrincipal;
import com.dsvn.starterkit.repositories.PasswordResetTokenRepository;
import com.dsvn.starterkit.repositories.UserRepository;
import com.dsvn.starterkit.services.MailService;
import com.dsvn.starterkit.services.TokenService;
import com.dsvn.starterkit.services.UserAuthenticationService;
import com.dsvn.starterkit.services.UserService;
import com.dsvn.starterkit.utils.CookieUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    @Autowired
    @Qualifier("uuidTokenService")
    private TokenService tokenService;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private UserRepository userRepository;

    @Autowired private UserService userService;

    @Autowired private UserRepository userJpaRepository;

    @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired private UrlGenerator urlGenerator;

    @Autowired private AppProperties appProperties;

    @Autowired private MailService mailService;

    @Override
    public UserDetails getUserLogin(LoginForm loginForm) {
        UserDTO userDTO =
                userRepository
                        .findByEmail(loginForm.getEmail())
                        .orElseThrow(
                                () ->
                                        new UsernameNotFoundException(
                                                "User not found with email : "
                                                        + loginForm.getEmail()));

        if (!passwordEncoder.matches(loginForm.getPassword(), userDTO.getPassword())) {
            throw new PasswordNotMatchesException("Password wrong : " + loginForm.getPassword());
        }

        return UserPrincipal.create(userDTO);
    }

    public UserDetails loadUserDetailsById(Long id) {
        return UserPrincipal.create(userService.loadUserById(id));
    }

    @Override
    public void changePassword(ChangePasswordForm changePasswordForm) {
        Long userId = AuthenticationHelper.currentUserId();
        User user = userJpaRepository.getOne(userId);

        if (!passwordEncoder.matches(changePasswordForm.getCurrentPassword(), user.getPassword())) {
            throw new PasswordNotMatchesException("Current password not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordForm.getNewPassword()));

        userJpaRepository.save(user);
    }

    @Override
    public String createToken(Authentication authentication, HttpServletResponse response) {
        String token = tokenService.createToken(authentication);

        CookieUtil.add(
                response,
                HttpHeaders.AUTHORIZATION,
                token,
                true,
                appProperties.getAuth().getTokenExpirationSec(),
                appProperties.getDomainPattern());

        return token;
    }

    @Override
    public void removeToken(HttpServletRequest request, HttpServletResponse response) {
        tokenService.removeToken(request);
        CookieUtil.clear(response, HttpHeaders.AUTHORIZATION);
    }

    @Override
    public String updateToken(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        tokenService.removeToken(request);

        return createToken(authentication, response);
    }

    @Override
    public void resetPassword(String email) {
        UserDTO userDTO =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new UsernameNotFoundException(
                                                "Email " + email + " does not exist"));

        LocalDateTime expiryDate =
                LocalDateTime.now().plusSeconds(appProperties.getPasswordResetTokenExpirationSec());

        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .userId(userDTO.getId())
                        .expiresAt(expiryDate)
                        .build();

        passwordResetTokenRepository.save(passwordResetToken);

        String resetPasswordUrl = urlGenerator.makeResetPasswordUrl(token);

        Map<String, String> templateData = new HashMap<>();
        templateData.put("reset_password_url", resetPasswordUrl);

        Mail mail = new Mail(MailTemplate.RESET_PASSWORD, templateData);
        mail.setTo(email);

        mailService.sendMail(mail);
    }
}
