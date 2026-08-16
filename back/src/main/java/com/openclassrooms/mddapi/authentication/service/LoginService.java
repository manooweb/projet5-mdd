package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.authentication.config.AuthenticationMessagesProperties;
import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.dto.LoginRequest;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationMessagesProperties messages;

  public LoginService(
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AuthenticationMessagesProperties messages) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.messages = messages;
  }

  @Transactional(readOnly = true)
  public String login(LoginRequest request) {
    UserAccount user =
        userAccountRepository
            .findByUsernameOrEmail(request.login(), request.login())
            .filter(account -> passwordEncoder.matches(request.password(), account.getPassword()))
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, messages.getInvalidCredentials()));

    return jwtService.createToken(user);
  }
}
