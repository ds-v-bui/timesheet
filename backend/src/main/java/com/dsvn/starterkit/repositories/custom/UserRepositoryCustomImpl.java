package com.dsvn.starterkit.repositories.custom;

import com.dsvn.starterkit.domains.forms.user.UserSearchForm;
import com.dsvn.starterkit.domains.models.user.Role;
import com.dsvn.starterkit.domains.models.user.UserDTO;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @Autowired private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Long save(UserDTO userDTO) {
        String query =
                "INSERT INTO users (email, first_name, last_name, password, phone_number, avatar, role, address, birth_day, gender)"
                        + " VALUES (:email, :first_name, :last_name, :password, :phone_number, :avatar, :role, :address, :birth_day, :gender)";

        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue("email", userDTO.getEmail())
                        .addValue("first_name", userDTO.getFirstName())
                        .addValue("last_name", userDTO.getLastName())
                        .addValue("password", userDTO.getPassword())
                        .addValue("phone_number", userDTO.getPhoneNumber())
                        .addValue("avatar", userDTO.getAvatar())
                        .addValue("role", userDTO.getRole().toString())
                        .addValue("address", userDTO.getAddress())
                        .addValue("birth_day", userDTO.getBirthday())
                        .addValue("gender", userDTO.getGender());

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(query, parameters, generatedKeyHolder);

        return Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
    }

    @Override
    public int[] saveAll(List<UserDTO> userDTOS) {
        String query =
                "INSERT INTO users (email, first_name, last_name, password, phone_number, avatar, role, address, birth_day, gender)"
                        + " VALUES (:email, :first_name, :last_name, :password, :phone_number, :avatar, :role, :address, :birth_day, :gender)";

        MapSqlParameterSource[] batchArgs =
                userDTOS.stream()
                        .map(
                                user ->
                                        new MapSqlParameterSource()
                                                .addValue("email", user.getEmail())
                                                .addValue("first_name", user.getFirstName())
                                                .addValue("last_name", user.getLastName())
                                                .addValue("password", user.getPassword())
                                                .addValue("phone_number", user.getPhoneNumber())
                                                .addValue("avatar", user.getAvatar())
                                                .addValue("role", user.getRole().toString())
                                                .addValue("address", user.getAddress())
                                                .addValue("birth_day", user.getBirthday())
                                                .addValue("gender", user.getGender()))
                        .toArray(MapSqlParameterSource[]::new);
        return namedJdbcTemplate.batchUpdate(query, batchArgs);
    }

    @Override
    public void update(UserDTO userDTO) {
        int count =
                namedJdbcTemplate.update(
                        "UPDATE users SET first_name = :first_name,"
                                + " last_name = :last_name,"
                                + " phone_number = :phone_number,"
                                + " address = :address,"
                                + " birth_day = :birth_day,"
                                + " gender = :gender WHERE id = :id",
                        new MapSqlParameterSource()
                                .addValue("id", userDTO.getId())
                                .addValue("first_name", userDTO.getFirstName())
                                .addValue("last_name", userDTO.getLastName())
                                .addValue("phone_number", userDTO.getPhoneNumber())
                                .addValue("address", userDTO.getAddress())
                                .addValue("birth_day", userDTO.getBirthday())
                                .addValue("gender", userDTO.getGender()));

        if (count != 1) {
            throw new EmptyResultDataAccessException(1);
        }
    }

    //    @Override
    //    public void deleteById(Long id) {
    //        int count =
    //                namedJdbcTemplate.update(
    //                        "DELETE FROM users WHERE id = :id",
    //                        new MapSqlParameterSource().addValue("id", id));
    //
    //        if (count != 1) {
    //            throw new EmptyResultDataAccessException(1);
    //        }
    //    }

    @Override
    public Page<UserDTO> findAll(UserSearchForm userSearchForm, Pageable pageable) {
        StringBuilder listQuery = selectQuery();
        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM users");

        StringBuilder whereCondition = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (userSearchForm.getEmail() != null) {
            whereCondition.append(" AND email LIKE :email");
            parameters.addValue("email", "%" + userSearchForm.getEmail() + "%");
        }

        if (userSearchForm.getFirstName() != null) {
            whereCondition.append(" AND first_name LIKE :firstName");
            parameters.addValue("firstName", "%" + userSearchForm.getFirstName() + "%");
        }

        if (userSearchForm.getBirthday() != null) {
            whereCondition.append(" AND birth_day = :birthday");
            parameters.addValue("birthday", userSearchForm.getBirthday());
        }

        listQuery.append(whereCondition);
        countQuery.append(whereCondition);

        listQuery.append(" LIMIT :offset,:limit");
        parameters.addValue("offset", pageable.getOffset());
        parameters.addValue("limit", pageable.getPageSize());

        List<UserDTO> userDTOS =
                namedJdbcTemplate.query(listQuery.toString(), parameters, rowMapperForUser());
        long total =
                namedJdbcTemplate.queryForObject(countQuery.toString(), parameters, Long.class);
        return new PageImpl<>(userDTOS, pageable, total);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        StringBuilder queryBuilder = selectQuery();
        queryBuilder.append(" WHERE email = :email");

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("email", email);

        List<UserDTO> userDTOS =
                namedJdbcTemplate.query(queryBuilder.toString(), parameters, rowMapperForUser());
        return CollectionUtils.isEmpty(userDTOS) ? Optional.empty() : Optional.of(userDTOS.get(0));
    }

    //    @Override
    //    public Optional<User> findById(Long id) {
    //        StringBuilder queryBuilder = selectQuery();
    //        queryBuilder.append(" WHERE id = :id");
    //
    //        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
    //
    //        List<User> users =
    //                namedJdbcTemplate.query(queryBuilder.toString(), parameters,
    // rowMapperForUser());
    //        return CollectionUtils.isEmpty(users) ? Optional.empty() : Optional.of(users.get(0));
    //    }

    @Override
    public boolean existsByEmail(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = :email";

        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("email", email);

        Long count = namedJdbcTemplate.queryForObject(query, parameters, Long.class);
        return count != null && count > 0;
    }

    private StringBuilder selectQuery() {
        return new StringBuilder(
                "SELECT id, first_name, last_name, phone_number, avatar, gender, "
                        + "email, birth_day, role, address, password FROM users");
    }

    private RowMapper<UserDTO> rowMapperForUser() {
        return (_rs, _rowNum) ->
                UserDTO.builder()
                        .id(_rs.getLong("id"))
                        .firstName(_rs.getString("first_name"))
                        .lastName((_rs.getString("last_name")))
                        .email(_rs.getString("email"))
                        .avatar(_rs.getString("avatar"))
                        .role(Role.valueOf(_rs.getString("role")))
                        .phoneNumber((_rs.getString("phone_number")))
                        .gender((_rs.getInt("gender")))
                        .address((_rs.getString("address")))
                        .birthday((_rs.getString("birth_day")))
                        .password((_rs.getString("password")))
                        .build();
    }
}
