package com.simple.taxi.auth.repository;

import com.simple.taxi.auth.model.entity.AuthUser;
import com.simple.taxi.auth.model.enums.LoginType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, String>, JpaSpecificationExecutor<AuthUser> {
    @EntityGraph(attributePaths = "user")
    Optional<AuthUser> findByIdAndLoginType(String id, LoginType loginType);
}