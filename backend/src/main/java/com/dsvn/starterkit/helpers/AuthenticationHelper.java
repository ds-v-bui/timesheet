package com.dsvn.starterkit.helpers;

import com.dsvn.starterkit.infrastructure.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationHelper {

    public static Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            return userPrincipal.getId();
        }

        throw new AccessDeniedException("Access Denied");
    }
}
