package com.dsvn.starterkit.repositories;

import com.dsvn.starterkit.domains.models.AccessToken;
import java.util.Optional;

public interface AccessTokenRepository {
    Long save(AccessToken token);

    Optional<AccessToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteExpiresToken();
}
