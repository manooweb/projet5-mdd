package com.openclassrooms.mddapi.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.dto.LoginRequest;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock private UserAccountRepository userAccountRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private UserAccount userAccount;

  private LoginService loginService;

  @BeforeEach
  void setUp() {
    loginService =
        new LoginService(userAccountRepository, passwordEncoder, jwtService, properties());
  }

  @Test
  void shouldCreateATokenForValidCredentials() {
    LoginRequest request = new LoginRequest("manu", "Pass1!wd");
    when(userAccountRepository.findByUsernameOrEmail("manu", "manu"))
        .thenReturn(Optional.of(userAccount));
    when(userAccount.getPassword()).thenReturn("encoded-password");
    when(passwordEncoder.matches("Pass1!wd", "encoded-password")).thenReturn(true);
    when(jwtService.createToken(userAccount)).thenReturn("token");

    assertThat(loginService.login(request)).isEqualTo("token");

    verify(jwtService).createToken(userAccount);
  }

  @Test
  void shouldRejectUnknownOrInvalidCredentials() {
    LoginRequest request = new LoginRequest("manu", "Pass1!wd");
    when(userAccountRepository.findByUsernameOrEmail("manu", "manu")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> loginService.login(request))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception -> {
              assertThat(exception.getMessageCode()).isEqualTo(ApiErrorCode.INVALID_CREDENTIALS);
              assertThat(exception.getStatus().value()).isEqualTo(401);
            });
  }

  private MddProperties properties() {
    MddProperties properties = new MddProperties();
    properties.getMessages().setInvalidCredentials("Invalid credentials.");
    return properties;
  }
}
