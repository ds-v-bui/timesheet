package com.dsvn.starterkit.services.impl;

import com.dsvn.starterkit.domains.models.AccessToken;
import com.dsvn.starterkit.exceptions.ResourceNotFoundException;
import com.dsvn.starterkit.infrastructure.configuration.AppProperties;
import com.dsvn.starterkit.infrastructure.security.UserPrincipal;
import com.dsvn.starterkit.repositories.AccessTokenRepository;
import com.dsvn.starterkit.services.TokenService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("uuidTokenService")
public class UuidTokenServiceImpl implements TokenService {

    @Autowired private AppProperties appProperties;

    @Autowired private AccessTokenRepository accessTokenRepository;

    @Override
    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        final String uuid = UUID.randomUUID().toString();

        LocalDateTime expiryDate =
                LocalDateTime.now().plusSeconds(appProperties.getAuth().getTokenExpirationSec());

        accessTokenRepository.save(
                AccessToken.builder()
                        .userId(userPrincipal.getId())
                        .expiresAt(expiryDate)
                        .token(uuid)
                        .build());

        return uuid;
    }

    @Override
    public Long getUserIdFromToken(String token) {
        AccessToken accessToken =
                accessTokenRepository
                        .findByToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("not found access token"));

        return accessToken.getUserId();
    }

    @Override
    public boolean validateToken(String token) {
        Optional<AccessToken> accessTokenOptional = accessTokenRepository.findByToken(token);
        if (accessTokenOptional.isEmpty()) {
            return false;
        }

        AccessToken accessToken = accessTokenOptional.get();
        return accessToken.isNonLocked() && accessToken.isNonExpired();
    }

    @Override
    public void removeToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);

        accessTokenRepository.deleteByToken(token);
    }
}
