package com.openclassrooms.mddapi.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.authentication.domain.UserAccount;
import com.openclassrooms.mddapi.authentication.repository.UserAccountRepository;
import com.openclassrooms.mddapi.authentication.service.JwtService;
import com.openclassrooms.mddapi.system.config.MddProperties;
import com.openclassrooms.mddapi.system.error.ApiErrorCode;
import com.openclassrooms.mddapi.system.error.ApiException;
import com.openclassrooms.mddapi.user.dto.CurrentUserResponse;
import com.openclassrooms.mddapi.user.dto.UpdateCurrentUserRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

  @Mock private UserAccountRepository userAccountRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private UserAccount userAccount;

  private CurrentUserService currentUserService;

  @BeforeEach
  void setUp() {
    currentUserService =
        new CurrentUserService(userAccountRepository, passwordEncoder, jwtService, properties());
  }

  @Test
  void shouldReturnTheCurrentUserProfile() {
    when(userAccountRepository.findById(12L)).thenReturn(Optional.of(userAccount));
    when(userAccount.getId()).thenReturn(12L);
    when(userAccount.getUsername()).thenReturn("manu");
    when(userAccount.getEmail()).thenReturn("manu@example.test");

    assertThat(currentUserService.getCurrentUser(12L))
        .isEqualTo(new CurrentUserResponse(12L, "manu", "manu@example.test"));
  }

  @Test
  void shouldRejectAnUnknownCurrentUser() {
    when(userAccountRepository.findById(12L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> currentUserService.getCurrentUser(12L))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode())
                    .isEqualTo(ApiErrorCode.AUTHENTICATION_REQUIRED));
  }

  @Test
  void shouldRejectAUsernameAlreadyUsedByAnotherUser() {
    UpdateCurrentUserRequest request =
        new UpdateCurrentUserRequest("other-user", "manu@example.test", "");
    when(userAccountRepository.findById(12L)).thenReturn(Optional.of(userAccount));
    when(userAccount.getUsername()).thenReturn("manu");
    when(userAccountRepository.existsByUsername("other-user")).thenReturn(true);

    assertThatThrownBy(() -> currentUserService.updateCurrentUser(12L, request))
        .isInstanceOfSatisfying(
            ApiException.class,
            exception ->
                assertThat(exception.getMessageCode()).isEqualTo(ApiErrorCode.DUPLICATE_IDENTITY));

    verify(userAccount, never()).update(any(), any(), any());
  }

  @Test
  void shouldUpdateTheProfileWithoutCreatingANewTokenWhenThePasswordIsUnchanged() {
    UpdateCurrentUserRequest request = new UpdateCurrentUserRequest("manu", "new@example.test", "");
    currentUser("manu", "manu@example.test");

    assertThat(currentUserService.updateCurrentUser(12L, request)).isEmpty();

    verify(userAccount).update("manu", "new@example.test", null);
    verify(userAccount, never()).invalidateSessions();
  }

  @Test
  void shouldInvalidateSessionsAndCreateATokenWhenThePasswordChanges() {
    UpdateCurrentUserRequest request =
        new UpdateCurrentUserRequest("manu", "manu@example.test", "Pass1!wd");
    currentUser("manu", "manu@example.test");
    when(userAccount.getPassword()).thenReturn("encoded-password");
    when(passwordEncoder.matches("Pass1!wd", "encoded-password")).thenReturn(false);
    when(passwordEncoder.encode("Pass1!wd")).thenReturn("new-encoded-password");
    when(jwtService.createToken(userAccount)).thenReturn("new-token");

    assertThat(currentUserService.updateCurrentUser(12L, request)).contains("new-token");

    verify(userAccount).update("manu", "manu@example.test", "new-encoded-password");
    verify(userAccount).invalidateSessions();
  }

  private void currentUser(String username, String email) {
    when(userAccountRepository.findById(12L)).thenReturn(Optional.of(userAccount));
    when(userAccount.getUsername()).thenReturn(username);
    when(userAccount.getEmail()).thenReturn(email);
  }

  private MddProperties properties() {
    MddProperties properties = new MddProperties();
    properties.getMessages().setDuplicateIdentity("Username or email is already used.");
    properties.getMessages().getErrors().setAuthenticationRequired("Authentication is required.");
    return properties;
  }
}
