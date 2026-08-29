package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.dto.RegisterRequest;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Creates accounts after validating that their username and email are unique. */
@Service
public class RegistrationService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final MddProperties properties;

  public RegistrationService(
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      MddProperties properties) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.properties = properties;
  }

  /**
   * Persists a new account and creates its first authenticated session.
   *
   * @param request validated registration data
   * @return a signed JWT for the new account
   * @throws ApiException when the username or email is already used
   */
  @Transactional
  public String register(RegisterRequest request) {
    if (userAccountRepository.existsByUsername(request.username())
        || userAccountRepository.existsByEmail(request.email())) {
      throw new ApiException(
          HttpStatus.CONFLICT,
          ApiErrorCode.DUPLICATE_IDENTITY,
          properties.getMessages().getDuplicateIdentity());
    }

    UserAccount user =
        UserAccount.register(
            request.username(), request.email(), passwordEncoder.encode(request.password()));
    user.initializeTimestamps();
    UserAccount savedUser = userAccountRepository.save(user);

    return jwtService.createToken(savedUser);
  }
}
