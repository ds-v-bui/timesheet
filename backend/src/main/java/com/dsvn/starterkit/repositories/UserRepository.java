package com.dsvn.starterkit.repositories;

import com.dsvn.starterkit.domains.entities.User;
import com.dsvn.starterkit.repositories.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long>, UserRepositoryCustom {}
