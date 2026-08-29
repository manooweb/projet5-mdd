package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.dto.LoginRequest;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Verifies credentials and creates a JWT for a valid existing account. */
@Service
public class LoginService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final MddProperties properties;

  public LoginService(
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
   * Authenticates a user by username or email.
   *
   * @param request validated login credentials
   * @return a signed JWT for the authenticated account
   * @throws ApiException when the account does not exist or the password does not match
   */
  @Transactional(readOnly = true)
  public String login(LoginRequest request) {
    UserAccount user =
        userAccountRepository
            .findByUsernameOrEmail(request.login(), request.login())
            .filter(account -> passwordEncoder.matches(request.password(), account.getPassword()))
            .orElseThrow(
                () ->
                    new ApiException(
                        HttpStatus.UNAUTHORIZED,
                        ApiErrorCode.INVALID_CREDENTIALS,
                        properties.getMessages().getInvalidCredentials()));

    return jwtService.createToken(user);
  }
}
