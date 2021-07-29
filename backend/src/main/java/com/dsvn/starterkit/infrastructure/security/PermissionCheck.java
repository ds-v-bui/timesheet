package com.dsvn.starterkit.infrastructure.security;

import com.dsvn.starterkit.domains.models.user.Role;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionCheck {
    public boolean permitAll() {
        return true;
    }

    public boolean isAuthenticated() {
        Object userPrincipal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userPrincipal instanceof UserPrincipal;
    }

    public boolean allowUser() {
        return allow(Role.user);
    }

    public boolean allowAdmin() {
        return allow(Role.admin);
    }

    public boolean allow(Role role) {
        Optional<UserPrincipal> userPrincipalOptional = getPrincipal();
        if (userPrincipalOptional.isEmpty()) {
            return false;
        }

        UserPrincipal userPrincipal = userPrincipalOptional.get();

        return userPrincipal.getAuthorities().stream()
                .anyMatch(authority -> role.toString().equals(authority.getAuthority()));
    }

    public Optional<UserPrincipal> getPrincipal() {
        Object userPrincipal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userPrincipal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) userPrincipal);
        }

        return Optional.empty();
    }
}
