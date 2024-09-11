package de.arbeitsagentur.opdt.keycloak.filestore.clientscope;

import static org.assertj.core.api.Assertions.*;

import de.arbeitsagentur.opdt.keycloak.filestore.model.KeycloakModelTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;

class FileClientScopeProviderTest extends KeycloakModelTest {

  private static final String REALM_ID = "desert";

  @Override
  protected void createEnvironment(KeycloakSession s) {
    s.realms().createRealm(REALM_ID);
  }

  @Override
  protected void cleanEnvironment(KeycloakSession s) {
    s.realms().removeRealm(REALM_ID);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"unknown"})
  void whenGetClientScopeById_givenUnknownId_thenReturnNull(String invalidId) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Act
          ClientScopeModel actual = clientScopes.getClientScopeById(realm, invalidId);
          // Assert
          assertThat(actual).isNull();
        });
  }

  @Test
  void whenGetClientScopeById_givenExistingId_thenReturnNotNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Arrange
          clientScopes.addClientScope(realm, "Kalahari");
          // Act
          ClientScopeModel actual = clientScopes.getClientScopeById(realm, "Kalahari");
          // Assert
          assertThat(actual).isNotNull();
        });
  }

  @Test
  void whenGetClientScopesStream_givenNoScopes_thenReturnEmptyStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Act
          Stream<ClientScopeModel> actual = clientScopes.getClientScopesStream(realm);
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenGetClientScopesStream_givenClientScopes_thenReturnStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (cs, realm) -> {
          // Arrange
          cs.addClientScope(realm, "Sahara");
          cs.addClientScope(realm, "Kalahari");
          cs.addClientScope(realm, "Gobi");
          // Act
          Stream<ClientScopeModel> actual = cs.getClientScopesStream(realm);
          // Assert
          assertThat(actual)
              .hasSize(3)
              .map(ClientScopeModel::getName)
              .containsExactlyInAnyOrder("Sahara", "Kalahari", "Gobi");
        });
  }

  @Test
  void whenAddClientScope_givenNull_then() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Act && Arrange
          assertThatExceptionOfType(IllegalArgumentException.class)
              .isThrownBy(() -> clientScopes.addClientScope(realm, null));
        });
  }

  @Test
  void whenAddClientScope_givenExistingId_thenThrowException() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScope, realm) -> {
          // Arrange
          clientScope.addClientScope(realm, "Atacama");
          // Act & Assert
          assertThatExceptionOfType(ModelDuplicateException.class)
              .isThrownBy(() -> clientScope.addClientScope(realm, "Atacama"));
        });
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"unknown"})
  void whenRemoveClientScope_givenNull_thenReturnFalse(String invalidId) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Act
          boolean actual = clientScopes.removeClientScope(realm, invalidId);
          // Assert
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenRemoveClientScope_givenScope_thenReturnTrue() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Arrange
          clientScopes.addClientScope(realm, "Sonora");
          // Act
          boolean actual = clientScopes.removeClientScope(realm, "Sonora");
          // Assert
          assertThat(actual).isTrue();
        });
  }

  @Test
  void whenRemoveClientScopes_givenNoScopes_thenNoExceptionIsThrown() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Act & Assert
          assertThat(clientScopes.getClientScopesStream(realm)).isEmpty();
          assertThatNoException().isThrownBy(() -> clientScopes.removeClientScopes(realm));
        });
  }

  @Test
  void whenRemoveClientScopes_givenScopes_thenScopesAreEmpty() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clientScopes, realm) -> {
          // Arrange
          clientScopes.addClientScope(realm, "Thar");
          clientScopes.addClientScope(realm, "Namib");
          // Act
          clientScopes.removeClientScopes(realm);
          // Assert
          var actual = clientScopes.getClientScopesStream(realm);
          assertThat(actual).isEmpty();
        });
  }
}
