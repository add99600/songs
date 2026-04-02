package org.example.backendspring.auth.repository;

import org.example.backendspring.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 JPA 리포지토리.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * OAuth 제공자와 subject로 사용자를 조회한다.
     */
    Optional<User> findByOauthProviderAndOauthSubject(String oauthProvider, String oauthSubject);

    /**
     * 이메일로 사용자를 조회한다.
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부를 확인한다.
     */
    boolean existsByEmail(String email);
}
