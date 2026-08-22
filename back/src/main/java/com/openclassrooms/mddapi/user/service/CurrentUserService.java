package com.openclassrooms.mddapi.user.service;

import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {

  private final UserAccountRepository userAccountRepository;
  private final MddProperties properties;

  public CurrentUserService(UserAccountRepository userAccountRepository, MddProperties properties) {
    this.userAccountRepository = userAccountRepository;
    this.properties = properties;
  }

  @Transactional(readOnly = true)
  public CurrentUserResponse getCurrentUser(Long userId) {
    return userAccountRepository
        .findById(userId)
        .map(CurrentUserResponse::from)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    ApiErrorCode.AUTHENTICATION_REQUIRED,
                    properties.getMessages().getErrors().getAuthenticationRequired()));
  }
}
