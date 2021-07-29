package com.dsvn.starterkit.infrastructure.dao;

import com.dsvn.starterkit.domains.models.AccessToken;
import com.dsvn.starterkit.repositories.AccessTokenRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class AccessTokenDAO implements AccessTokenRepository {

    @Autowired private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Long save(AccessToken token) {
        String query =
                "INSERT INTO access_token (token, expires_at, locked, user_id)"
                        + " VALUES (:token, :expiresAt, :locked, :userId)";

        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue("token", token.getToken())
                        .addValue("expiresAt", token.getExpiresAt())
                        .addValue("locked", token.getLocked())
                        .addValue("userId", token.getUserId());

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(query, parameters, generatedKeyHolder);

        return Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
    }

    @Override
    public Optional<AccessToken> findByToken(String token) {
        StringBuilder queryBuilder = selectQuery();
        queryBuilder.append(" WHERE token = :token");

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("token", token);

        List<AccessToken> users =
                namedJdbcTemplate.query(queryBuilder.toString(), parameters, rowMapperForUser());
        return CollectionUtils.isEmpty(users) ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public void deleteByToken(String token) {
        namedJdbcTemplate.update(
                "DELETE FROM access_token WHERE token = :token",
                new MapSqlParameterSource().addValue("token", token));
    }

    @Override
    public void deleteExpiresToken() {
        namedJdbcTemplate.update(
                "DELETE FROM access_token WHERE expires_at < now()", new MapSqlParameterSource());
    }

    private StringBuilder selectQuery() {
        return new StringBuilder("SELECT id, token, expires_at, locked, user_id FROM access_token");
    }

    private RowMapper<AccessToken> rowMapperForUser() {
        return (_rs, _rowNum) ->
                AccessToken.builder()
                        .id(_rs.getLong("id"))
                        .token((_rs.getString("token")))
                        .expiresAt(_rs.getTimestamp("expires_at").toLocalDateTime())
                        .locked(_rs.getBoolean("locked"))
                        .userId(_rs.getLong("user_id"))
                        .build();
    }
}
