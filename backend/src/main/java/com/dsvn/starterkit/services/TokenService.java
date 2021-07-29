package com.dsvn.starterkit.services;

import com.dsvn.starterkit.utils.CookieUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

public interface TokenService {

    String createToken(Authentication authentication);

    Long getUserIdFromToken(String token);

    boolean validateToken(String token);

    void removeToken(HttpServletRequest request);

    default String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return CookieUtil.getValue(request, HttpHeaders.AUTHORIZATION);
    }
}
