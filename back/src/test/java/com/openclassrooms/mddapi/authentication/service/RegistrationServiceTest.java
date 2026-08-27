package com.openclassrooms.mddapi.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.dto.RegisterRequest;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @Mock private UserAccountRepository userAccountRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;

  private RegistrationService registrationService;

  @BeforeEach
  void setUp() {
    registrationService =
        new RegistrationService(userAccountRepository, passwordEncoder, jwtService, properties());
  }

  @Test
  void shouldRegisterTheUserWithAnEncodedPasswordAndCreateAToken() {
    RegisterRequest request = new RegisterRequest("manu", "manu@example.test", "Pass1!wd");
    when(passwordEncoder.encode("Pass1!wd")).thenReturn("encoded-password");
    when(userAccountRepository.save(any(UserAccount.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(jwtService.createToken(any(UserAccount.class))).thenReturn("token");

    assertThat(registrationService.register(request)).isEqualTo("token");

    ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
    verify(userAccountRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getUsername()).isEqualTo("manu");
    assertThat(userCaptor.getValue().getEmail()).isEqualTo("manu@example.test");
    assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
  }

  @Test
  void shouldRejectAnExistingUsername() {
    RegisterRequest request = new RegisterRequest("manu", "manu@example.test", "Pass1!wd");
    when(userAccountRepository.existsByUsername("manu")).thenReturn(true);

    assertThatThrownBy(() -> registrationService.register(request))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode()).isEqualTo(ApiErrorCode.DUPLICATE_IDENTITY));

    verify(userAccountRepository, never()).save(any());
  }

  @Test
  void shouldRejectAnExistingEmail() {
    RegisterRequest request = new RegisterRequest("manu", "manu@example.test", "Pass1!wd");
    when(userAccountRepository.existsByEmail("manu@example.test")).thenReturn(true);

    assertThatThrownBy(() -> registrationService.register(request))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode()).isEqualTo(ApiErrorCode.DUPLICATE_IDENTITY));
  }

  private MddProperties properties() {
    MddProperties properties = new MddProperties();
    properties.getMessages().setDuplicateIdentity("Username or email is already used.");
    return properties;
  }
}
