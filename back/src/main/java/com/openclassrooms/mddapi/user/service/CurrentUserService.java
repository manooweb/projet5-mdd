package com.openclassrooms.mddapi.user.service;

import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

  private final UserAccountRepository userAccountRepository;

  public CurrentUserService(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  @Transactional(readOnly = true)
  public CurrentUserResponse getCurrentUser(Long userId) {
    return userAccountRepository
        .findById(userId)
        .map(CurrentUserResponse::from)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }
}
