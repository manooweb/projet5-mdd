package com.openclassrooms.mddapi.authentication.service;

import com.openclassrooms.mddapi.authentication.config.AuthenticationMessagesProperties;
import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.dto.RegisterRequest;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistrationService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationMessagesProperties messages;

  public RegistrationService(
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AuthenticationMessagesProperties messages) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.messages = messages;
  }

  @Transactional
  public String register(RegisterRequest request) {
    if (userAccountRepository.existsByUsername(request.username())
        || userAccountRepository.existsByEmail(request.email())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, messages.getDuplicateIdentity());
    }

    UserAccount user =
        UserAccount.register(
            request.username(), request.email(), passwordEncoder.encode(request.password()));
    user.initializeTimestamps();
    UserAccount savedUser = userAccountRepository.save(user);

    return jwtService.createToken(savedUser);
  }
}
