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

package de.arbeitsagentur.opdt.keycloak.filestore.group;

import static org.assertj.core.api.Assertions.assertThat;

import de.arbeitsagentur.opdt.keycloak.filestore.KeycloakModelTest;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;

class FileGroupProviderTest extends KeycloakModelTest {

  private static final String REALM_ID = "river";

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
  void whenGetGroupById_givenInvalidParam_thenReturnNull(String invalid) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          var actual = groups.getGroupById(realm, invalid);
          assertThat(actual).isNull();
        });
  }

  @Test
  void whenGetGroupById_givenExistingGroup_thenReturnNotNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          groups.createGroup(realm, "Nile");
          var actual = groups.getGroupById(realm, "Nile");
          assertThat(actual.getId()).isEqualTo("Nile");
        });
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"unknown"})
  void whenGetGroupByName_givenInvalidParam_thenReturnNull(String invalid) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          var actual = groups.getGroupByName(realm, null, invalid);
          assertThat(actual).isNull();
        });
  }

  @Test
  void whenGetGroupByName_givenExistingGroup_thenReturnNotNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          groups.createGroup(realm, "Amazon");
          var actual = groups.getGroupByName(realm, null, "Amazon");
          assertThat(actual.getId()).isEqualTo("Amazon");
        });
  }

  @Test
  void whenGetGroupByName_givenParentGroup_thenReturnNotNull() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          var parent = groups.createGroup(realm, "Parent");
          groups.createGroup(realm, "Child").setParent(parent);
          // Act
          var actual = groups.getGroupByName(realm, parent, "Child");
          // Assert
          assertThat(actual.getId()).isEqualTo("Child");
        });
  }

  @Test
  void whenGetGroupsStream_givenNoGroups_thenReturnEmptyStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Act
          var actual = groups.getGroupsStream(realm);
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenGetGroupsStream_givenGroups_thenReturnStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          groups.createGroup(realm, "Yukon");
          groups.createGroup(realm, "Rio-Grande");
          // Act
          var actual = groups.getGroupsStream(realm);
          // Assert
          assertThat(actual)
              .hasSize(2)
              .map(GroupModel::getName)
              .containsExactly("Rio-Grande", "Yukon");
        });
  }

  @Test
  void whenGetGroupsStreamWithIds_givenNoGroups_thenReturnEmptyStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Act
          var actual = groups.getGroupsStream(realm, Stream.of());
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenGetGroupsStreamWithIds_givenUnknownGroups_thenReturnEmptyStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Act
          var actual = groups.getGroupsStream(realm, Stream.of("unknown"));
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenGetGroupsStreamWithIds_givenGroups_thenReturnStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          groups.createGroup(realm, "Yukon");
          groups.createGroup(realm, "Rio-Grande");
          // Act
          var actual = groups.getGroupsStream(realm, Stream.of("Yukon"));
          // Assert
          assertThat(actual).hasSize(1).map(GroupModel::getName).containsExactly("Yukon");
        });
  }

  @Test
  void whenGetGroupsStreamWithIds_givenSearchPattern_thenReturnStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          groups.createGroup(realm, "patty"); // partial -> exclude
          groups.createGroup(realm, "no-match"); // no match -> exclude
          groups.createGroup(realm, "abc-pattern");
          groups.createGroup(realm, "abc-pattern-xyz");
          groups.createGroup(realm, "pattern");
          groups.createGroup(realm, "pattern-xyz");
          // Act
          var ids =
              Stream.of(
                  "patty", "no-match", "pattern", "pattern-xyz", "abc-pattern", "abc-pattern-xyz");
          var actual = groups.getGroupsStream(realm, ids, "pattern", null, null);
          // Assert
          assertThat(actual)
              .hasSize(4)
              .map(GroupModel::getName)
              .containsExactly("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
        });
  }

  @Test
  void whenGetGroupsCount_givenNoGroup_thenReturnZero() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Act
          Long actual = groups.getGroupsCount(realm, false);
          // Assert
          assertThat(actual).isZero();
        });
  }

  @Test
  void whenGetGroupsCount_givenGroups_thenReturnCount() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          groups.createGroup(realm, "Ob");
          groups.createGroup(realm, "Niger");
          // Act
          Long actual = groups.getGroupsCount(realm, false);
          // Assert
          assertThat(actual).isEqualTo(2);
        });
  }

  @Test
  void whenGetGroupsCount_givenTopGroups_thenReturnTopCount() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          var parent = groups.createGroup(realm, "Ob");
          groups.createGroup(realm, "Niger", parent);
          // Act
          Long actual = groups.getGroupsCount(realm, true);
          // Assert
          assertThat(actual).isEqualTo(1);
        });
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"unknown"})
  void whenGetGroupsCountByNameContaining_givenInvalidParam_thenReturnZero(String invalid) {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Act
          Long actual = groups.getGroupsCountByNameContaining(realm, invalid);
          // Assert
          assertThat(actual).isZero();
        });
  }

  @Test
  void whenGetGroupsCountByNameContaining_givenGroups_thenReturnCount() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          groups.createGroup(realm, "patty"); // partial -> no match
          groups.createGroup(realm, "abc-pattern");
          groups.createGroup(realm, "abc-pattern-xyz");
          groups.createGroup(realm, "pattern");
          groups.createGroup(realm, "pattern-xyz");
          // Act
          Long actual = groups.getGroupsCountByNameContaining(realm, "pattern");
          // Assert
          assertThat(actual).isEqualTo(4);
        });
  }

  @Test
  void whenGetGroupsByRoleStream_givenNoGroups_thenReturnEmptyStreams() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Arrange
          var role = session.roles().addRealmRole(realm, "unassigned");
          // Act
          var actual = session.groups().getGroupsByRoleStream(realm, role, null, null);
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenGetGroupsByRoleStream_givenGroups_thenReturnStreams() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Arrange
          var role = session.roles().addRealmRole(realm, "Fisherman");
          session.groups().createGroup(realm, "Orinoco").grantRole(role);
          // Act
          var actual = session.groups().getGroupsByRoleStream(realm, role, null, null);
          // Assert
          assertThat(actual).hasSize(1).map(GroupModel::getName).containsExactly("Orinoco");
        });
  }

  @Test
  void whenGetTopLevelGroupsStream_givenNoGroups_thenReturnEmptyStream() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Act
          var actual = session.groups().getTopLevelGroupsStream(realm);
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenGetTopLevelGroupsStream_giveParentChildGroups_thenReturnStream() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Arrange
          session.groups().createGroup(realm, "Tigris");
          var parent = session.groups().createGroup(realm, "Nile");
          session.groups().createGroup(realm, "Blue-Nile", parent);
          // Act
          var actual = session.groups().getTopLevelGroupsStream(realm);
          // Assert
          assertThat(actual).hasSize(2).map(GroupModel::getName).containsExactly("Nile", "Tigris");
        });
  }

  @Test
  void whenGetTopLevelGroupsStream_givenPagination_thenReturnStream() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Arrange
          session.groups().createGroup(realm, "Colorado");
          session.groups().createGroup(realm, "Nile"); // start - firstResult = 1
          session.groups().createGroup(realm, "Tigris"); // end - maxResults = 2
          session.groups().createGroup(realm, "Yangtze");
          // Act
          var actual = session.groups().getTopLevelGroupsStream(realm, 1, 2);
          // Assert
          assertThat(actual).hasSize(2).map(GroupModel::getName).containsExactly("Nile", "Tigris");
        });
  }

  @Test
  void whenGetTopLevelGroupsStream_givenSearchPattern_thenReturnStream() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Arrange
          session.groups().createGroup(realm, "patty");
          session.groups().createGroup(realm, "abc-pattern");
          session.groups().createGroup(realm, "abc-pattern-xyz");
          session.groups().createGroup(realm, "pattern");
          session.groups().createGroup(realm, "pattern-xyz");
          // Act
          var actual =
              session.groups().getTopLevelGroupsStream(realm, "pattern", false, null, null);
          // Assert
          assertThat(actual)
              .hasSize(4)
              .map(GroupModel::getName)
              .containsExactly("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
        });
  }

  @Test
  void whenGetTopLevelGroupsStream_givenExactSearch_thenReturnStream() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Arrange
          session.groups().createGroup(realm, "patty");
          session.groups().createGroup(realm, "abc-pattern");
          session.groups().createGroup(realm, "abc-pattern-xyz");
          session.groups().createGroup(realm, "pattern");
          session.groups().createGroup(realm, "pattern-xyz");
          // Act
          var actual =
              session.groups().getTopLevelGroupsStream(realm, "pattern", false, null, null);
          // Assert
          assertThat(actual)
              .hasSize(4)
              .map(GroupModel::getName)
              .containsExactly("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
        });
  }

  @Test
  void whenSearchGroupsByAttributes_givenNoGroups_thenReturnEmptyStream() {
    withRealm(
        REALM_ID,
        (session, realm) -> {
          // Act
          var actual = session.groups().searchGroupsByAttributes(realm, Map.of(), null, null);
          // Assert
          assertThat(actual).isEmpty();
        });
  }

  @Test
  void whenSearchGroupsByAttributes_givenGroups_thenReturnStream() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          groups.createGroup(realm, "Rhine").setAttribute("key", List.of("value"));
          groups.createGroup(realm, "Nile").setAttribute("key", List.of("value"));
          groups.createGroup(realm, "Amazon").setAttribute("no-match", List.of("value"));
          // Act
          var actual = groups.searchGroupsByAttributes(realm, Map.of("key", "value"), null, null);
          // Assert
          assertThat(actual).hasSize(2).map(GroupModel::getName).containsExactly("Nile", "Rhine");
        });
  }

  @Test
  void whenRemoveGroup_givenNull_thenReturnFalse() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Act
          var actual = groups.removeGroup(realm, null);
          // Assert
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenRemoveGroup_givenGroup_thenReturnTrue() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          var group = groups.createGroup(realm, "Ganges");
          // Act
          var actual = groups.removeGroup(realm, group);
          // Assert
          assertThat(actual).isTrue();
          assertThat(groups.getGroupByName(realm, null, "Ganges")).isNull();
        });
  }

  @Test
  void whenRemoveGroup_givenAlreadyRemovedGroup_thenReturnFalse() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          var group = groups.createGroup(realm, "Ganges");
          groups.removeGroup(realm, group); // remove intentionally
          // Act
          var actual = groups.removeGroup(realm, group);
          // Assert
          assertThat(actual).isFalse();
        });
  }

  @Test
  void whenAddTopLevelGroup_givenChild_thenChildIsGrownUp() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          var parent = groups.createGroup(realm, "Parent");
          var child = groups.createGroup(realm, "Child", parent);
          assertThat(groups.getGroupsCount(realm, /* onlyTopGroups */ true)).isEqualTo(1);
          // Act
          groups.addTopLevelGroup(realm, child);
          // Assert
          assertThat(child.getParentId()).isNull();
          assertThat(groups.getGroupsCount(realm, /* onlyTopGroups */ true)).isEqualTo(2);
        });
  }

  @Test
  void whenMoveGroup_givenGroups_thenGroupIsMoved() {
    withRealmAndProvider(
        REALM_ID,
        KeycloakSession::groups,
        (groups, realm) -> {
          // Arrange
          var previousParent = groups.createGroup(realm, "Parent1");
          var nextParent = groups.createGroup(realm, "Parent2");
          var fosterChild = groups.createGroup(realm, "Child", previousParent);
          // Act
          groups.moveGroup(realm, fosterChild, nextParent);
          // Assert
          assertThat(fosterChild.getParentId()).isEqualTo(nextParent.getId());
        });
  }
}
