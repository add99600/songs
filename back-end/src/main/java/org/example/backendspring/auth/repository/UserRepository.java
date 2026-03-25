package org.example.backendspring.auth.repository;

import org.example.backendspring.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOauthProviderAndOauthSubject(String oauthProvider, String oauthSubject);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
