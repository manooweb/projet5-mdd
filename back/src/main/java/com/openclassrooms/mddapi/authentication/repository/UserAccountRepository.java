package com.openclassrooms.mddapi.authentication.repository;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
