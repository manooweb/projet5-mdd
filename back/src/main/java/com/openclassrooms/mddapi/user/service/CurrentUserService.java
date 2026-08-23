package com.openclassrooms.mddapi.user.service;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.authentication.service.JwtService;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import com.openclassrooms.mddapi.user.dto.UpdateCurrentUserRequest;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final MddProperties properties;

  public CurrentUserService(
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      MddProperties properties) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.properties = properties;
  }

  @Transactional(readOnly = true)
  public CurrentUserResponse getCurrentUser(Long userId) {
    return CurrentUserResponse.from(findCurrentUser(userId));
  }

  @Transactional
  public Optional<String> updateCurrentUser(Long userId, UpdateCurrentUserRequest request) {
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

    boolean passwordChanged =
        !request.password().isEmpty()
            && !passwordEncoder.matches(request.password(), currentUser.getPassword());
    String encodedPassword =
        request.password().isEmpty() ? null : passwordEncoder.encode(request.password());
    currentUser.update(request.username(), request.email(), encodedPassword);

    if (!passwordChanged) {
      return Optional.empty();
    }

    currentUser.invalidateSessions();
    return Optional.of(jwtService.createToken(currentUser));
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
