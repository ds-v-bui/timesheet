package com.dsvn.starterkit.services.impl;

import com.dsvn.starterkit.infrastructure.configuration.AppProperties;
import com.dsvn.starterkit.infrastructure.security.UserPrincipal;
import com.dsvn.starterkit.services.TokenService;
import io.jsonwebtoken.*;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service("jwtTokenService")
public class JwtTokenServiceImpl implements TokenService {

    @Autowired private AppProperties appProperties;

    @Override
    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate =
                new Date(now.getTime() + appProperties.getAuth().getTokenExpirationSec() * 1000);

        return Jwts.builder()
                .setSubject(String.valueOf(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();
    }

    @Override
    public Long getUserIdFromToken(String token) {
        Claims claims =
                Jwts.parser()
                        .setSigningKey(appProperties.getAuth().getTokenSecret())
                        .parseClaimsJws(token)
                        .getBody();

        return Long.parseLong(claims.getSubject());
    }

    @Override
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(appProperties.getAuth().getTokenSecret())
                    .parseClaimsJws(authToken);

            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    @Override
    public void removeToken(HttpServletRequest request) {}
}
