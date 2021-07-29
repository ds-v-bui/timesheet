package com.dsvn.starterkit.infrastructure.security.filter;

import com.dsvn.starterkit.services.TokenService;
import com.dsvn.starterkit.services.UserAuthenticationService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Qualifier("uuidTokenService")
    @Autowired
    private TokenService tokenService;

    @Autowired private UserAuthenticationService userAuthenticationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = tokenService.getTokenFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenService.validateToken(jwt)) {
                Long userId = tokenService.getUserIdFromToken(jwt);

                UserDetails userDetails = userAuthenticationService.loadUserDetailsById(userId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }
}
