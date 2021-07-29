package com.dsvn.starterkit.helpers;

import com.dsvn.starterkit.infrastructure.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UrlGenerator {

    private final String RESET_PASSWORD_PATH = "reset-password";

    @Autowired private AppProperties appProperties;

    public String makeResetPasswordUrl(String token) {
        return appProperties.getFrontEndUrl() + "/" + RESET_PASSWORD_PATH + "?token=" + token;
    }
}
