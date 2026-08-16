package com.openclassrooms.mddapi.authentication.repository;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  Optional<UserAccount> findByUsernameOrEmail(String username, String email);
}
