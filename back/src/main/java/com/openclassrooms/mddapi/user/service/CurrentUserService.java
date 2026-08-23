package com.openclassrooms.mddapi.user.service;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import com.openclassrooms.mddapi.user.dto.UpdateCurrentUserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final MddProperties properties;

  public CurrentUserService(
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder,
      MddProperties properties) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.properties = properties;
  }

  @Transactional(readOnly = true)
  public CurrentUserResponse getCurrentUser(Long userId) {
    return CurrentUserResponse.from(findCurrentUser(userId));
  }

  @Transactional
  public void updateCurrentUser(Long userId, UpdateCurrentUserRequest request) {
    UserAccount currentUser = findCurrentUser(userId);
    if ((!currentUser.getUsername().equals(request.username())
            && userAccountRepository.existsByUsername(request.username()))
        || (!currentUser.getEmail().equals(request.email())
            && userAccountRepository.existsByEmail(request.email()))) {
      throw new ApiException(
          HttpStatus.CONFLICT,
          ApiErrorCode.DUPLICATE_IDENTITY,
          properties.getMessages().getDuplicateIdentity());
    }

    String encodedPassword =
        request.password().isEmpty() ? null : passwordEncoder.encode(request.password());
    currentUser.update(request.username(), request.email(), encodedPassword);
  }

  private UserAccount findCurrentUser(Long userId) {
    return userAccountRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    ApiErrorCode.AUTHENTICATION_REQUIRED,
                    properties.getMessages().getErrors().getAuthenticationRequired()));
  }
}
