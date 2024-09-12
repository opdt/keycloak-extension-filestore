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

package de.arbeitsagentur.opdt.keycloak.filestore.clientscope;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import de.arbeitsagentur.opdt.keycloak.filestore.KeycloakModelTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;

class FileClientScopeAdapterTest extends KeycloakModelTest {

  private static final String REALM_ID = "zoo";

  @Override
  protected void createEnvironment(KeycloakSession s) {
    s.realms().createRealm(REALM_ID);
  }

  @Override
  protected void cleanEnvironment(KeycloakSession s) {
    s.realms().removeRealm(REALM_ID);
  }

  @Test
  void whenSetName_thenGet() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "gorilla");
          sut.setName("godzilla minus one");
          assertThat(sut.getName()).isEqualTo("godzilla_minus_one");
        });
  }

  @Test
  void whenSetDescription_thenGet() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "gorilla");
          sut.setDescription("Large monkey");
          assertThat(sut.getDescription()).isEqualTo("Large monkey");
        });
  }

  @Test
  void whenSetAttributes_givenUnknownKey_thenNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "gorilla");
          assertThat(sut.getAttribute("unknown")).isNull();
        });
  }

  @Test
  void whenSetAttributes_thenGet() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "gorilla");
          sut.setAttribute("color", "silver");
          assertThat(sut.getAttribute("color")).isEqualTo("silver");
        });
  }

  @Test
  void whenRemoveAttributes_givenNoAttr_thenNothingHappens() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "gorilla");
          // Act & Assert
          assertThatNoException().isThrownBy(() -> sut.removeAttribute("color"));
        });
  }

  @Test
  void whenRemoveAttributes_thenNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "gorilla");
          sut.setAttribute("color", "silver");
          // Act
          sut.removeAttribute("color");
          // Assert
          assertThat(sut.getAttribute("color")).isNull();
        });
  }

  @Test
  void whenGetAttributes_givenNoAttributes_thenReturnEmptyStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "giraffe");
          // Act
          var actual = sut.getAttributes();
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenGetAttributes_givenAttributes_thenReturnStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::clientScopes,
        (clients, realm) -> {
          var sut = clients.addClientScope(realm, "giraffe");
          sut.setAttribute("height", "tall");
          sut.setAttribute("color", "yellow-brown");
          // Act
          var actual = sut.getAttributes();
          // Assert
          assertThat(actual)
              .hasSize(2)
              .contains(Map.entry("height", "tall"), Map.entry("color", "yellow-brown"));
        });
  }
}
