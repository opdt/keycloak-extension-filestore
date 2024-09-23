/*
 * Copyright 2024. IT-Systemhaus der Bundesagentur fuer Arbeit
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.MapAssert.assertThatMap;

import de.arbeitsagentur.opdt.keycloak.filestore.KeycloakModelTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;

class FileRealmProviderTest extends KeycloakModelTest {

  private static final String REALM_ID = "mountain";

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
  void whenCreateRealm_givenInvalidName_thenExceptionIsThrown(String invalid) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
              .isThrownBy(() -> realms.createRealm(REALM_ID, invalid));
        });
  }

  @Test
  void whenCreateRealm_givenExistingId_thenExceptionIsThrown() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          assertThatExceptionOfType(ModelDuplicateException.class)
              .isThrownBy(() -> realms.createRealm(REALM_ID, REALM_ID));
        });
  }

  @Test
  void whenCreateRealm_givenExistingName_thenExceptionIsThrown() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          assertThatExceptionOfType(ModelDuplicateException.class)
              .isThrownBy(() -> realms.createRealm(null, REALM_ID));
        });
  }

  @Test
  void whenCreateRealm_givenNullId_thenSetNameAsId() {
    inCommittedTransaction(
        session -> {
          var actual = session.realms().createRealm(null, "K2");
          assertThat(actual.getId()).isEqualTo("K2");

          // Teardown
          session.realms().removeRealm("K2");
        });
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"unknown"})
  void whenGetRealm_givenNull_thenReturnNull(String invalid) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          var actual = realms.getRealm(invalid);
          assertThat(actual).isNull();
        });
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"unknown"})
  void whenGetRealmByName_givenNull_thenReturnNull(String invalid) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          var actual = realms.getRealmByName(invalid);
          assertThat(actual).isNull();
        });
  }

  @Test
  void whenGetRealmByName_givenNull_thenReturnNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          var actual = realms.getRealmByName(REALM_ID);
          assertThat(actual.getId()).isEqualTo(REALM_ID);
        });
  }

  @Test
  void whenGetRealmsWithProviderTypeStream_givenObjectClass_thenReturnEmptyStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          var actual = realms.getRealmsWithProviderTypeStream(Object.class);
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenRemoveRealm_givenNoRealm_thenReturnFalse() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          var actual = realms.removeRealm("unknown");
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenRemoveExpiredClientInitialAccess_givenMultipleClients_thenExpiredClientsAreRemoved() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Arrange
          realms.createClientInitialAccessModel(realm, -5000, 0); // expired
          realms.createClientInitialAccessModel(realm, -1000, 0); // expired
          realms.createClientInitialAccessModel(realm, -1, 0); // expired
          realms.createClientInitialAccessModel(realm, 1000, 0);
          assertThat(realm.getClientInitialAccesses()).hasSize(4);
          // Act
          realms.removeExpiredClientInitialAccess();
          // Assert
          assertThat(realm.getClientInitialAccesses()).hasSize(1);
        });
  }

  @Test
  void whenSaveLocalizationText_givenNull_thenNoLocalizationIsSet() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          realms.saveLocalizationText(realm, null, null, null);
          // Assert
          assertThatMap(realm.getRealmLocalizationTextsByLocale("de-de")).isEmpty();
        });
  }

  @Test
  void whenSaveLocalizationText_givenValues_thenLocalizationIsSet() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          realms.saveLocalizationText(realm, "de-de", "TREE", "Baum");
          // Assert
          assertThatMap(realm.getRealmLocalizationTextsByLocale("de-de"))
              .contains(Map.entry("TREE", "Baum"));
        });
  }

  @Test
  void whenSaveLocalizationTexts_givenEmptyMap_thenNoLocalizationsAreSet() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          realms.saveLocalizationTexts(realm, "de-de", Map.of());
          // Assert
          assertThatMap(realm.getRealmLocalizationTextsByLocale("de-de")).isEmpty();
        });
  }

  @Test
  void whenSaveLocalizationTexts_givenMultipleValues_thenLocalizationsAreSet() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          Map<String, String> localizationTexts =
              Map.of(
                  "TREE", "Baum",
                  "HOUSE", "Haus",
                  "LAKE", "See");
          realms.saveLocalizationTexts(realm, "de-de", localizationTexts);
          // Assert
          assertThatMap(realm.getRealmLocalizationTextsByLocale("de-de"))
              .contains(
                  Map.entry("TREE", "Baum"), Map.entry("HOUSE", "Haus"), Map.entry("LAKE", "See"));
        });
  }

  @Test
  void whenUpdateLocalizationText_givenInvalidValue_thenReturnFalse() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          var actual = realms.updateLocalizationText(realm, null, null, null);
          // Assert
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenUpdateLocalizationText_givenUnknownKey_thenReturnFalse() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          var actual = realms.updateLocalizationText(realm, "de-de", "unknown", "unbekannt");
          // Assert
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenUpdateLocalizationText_givenValue_thenLocalizationIsUpdated() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Arrange
          realms.saveLocalizationText(realm, "de-de", "COCK", "Gockel");
          // Act
          var actual = realms.updateLocalizationText(realm, "de-de", "COCK", "Hahn");
          // Assert
          assertThat(actual).isTrue();
          assertThatMap(realm.getRealmLocalizationTextsByLocale("de-de"))
              .contains(Map.entry("COCK", "Hahn"));
        });
  }

  @Test
  void whenDeleteLocalizationTextsByLocale_givenValue_thenLocalizationIsDeleted() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Arrange
          realms.saveLocalizationText(realm, "de-de", "TABLE", "Tisch");
          realms.saveLocalizationText(realm, "de-de", "CHAIR", "Stuhl");
          // Act
          var actual = realms.deleteLocalizationTextsByLocale(realm, "de-de");
          // Assert
          assertThat(actual).isTrue();
          assertThatMap(realm.getRealmLocalizationTextsByLocale("de-de")).isEmpty();
        });
  }

  @Test
  void whenDeleteLocalizationText_givenNullValue_thenReturnFalse() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          var actual = realms.deleteLocalizationText(realm, null, null);
          // Assert
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenDeleteLocalizationText_givenUnknownKey_thenReturnFalse() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          var actual = realms.deleteLocalizationText(realm, "de-de", "unknown");
          // Assert
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenDeleteLocalizationText_givenValue_thenLocalizationIsDeleted() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Arrange
          realms.saveLocalizationText(realm, "de-de", "TABLE", "Tisch");
          realms.saveLocalizationText(realm, "de-de", "CHAIR", "Stuhl");
          // Act
          var actual = realms.deleteLocalizationText(realm, "de-de", "TABLE");
          // Assert
          assertThat(actual).isTrue();
          assertThatMap(realm.getRealmLocalizationTextsByLocale("de-de"))
              .hasSize(1)
              .contains(Map.entry("CHAIR", "Stuhl"));
        });
  }

  @Test
  void whenGetLocalizationText_givenNull_thenReturnNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          var actual = realms.getLocalizationTextsById(realm, null, null);
          // Assert
          assertThat(actual).isNull();
        });
  }

  @Test
  void whenGetLocalizationText_givenUnknownKey_thenReturnNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Act
          var actual = realms.getLocalizationTextsById(realm, "en-us", "unknown");
          // Assert
          assertThat(actual).isNull();
        });
  }

  @Test
  void whenGetLocalizationText_givenValue_thenReturnValue() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::realms,
        (realms, realm) -> {
          // Arrange
          realms.saveLocalizationText(realm, "de-de", "TABLE", "Tisch");
          realms.saveLocalizationText(realm, "en-us", "CHAIR", "chair");
          // Act
          var actual = realms.getLocalizationTextsById(realm, "de-de", "TABLE");
          // Assert
          assertThat(actual).isEqualTo("Tisch");
        });
  }
}
