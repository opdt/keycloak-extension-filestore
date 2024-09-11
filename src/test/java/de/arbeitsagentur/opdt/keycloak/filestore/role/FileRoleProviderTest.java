package de.arbeitsagentur.opdt.keycloak.filestore.role;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.RoleModel;
import de.arbeitsagentur.opdt.keycloak.filestore.model.KeycloakModelTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FileRoleProviderTest extends KeycloakModelTest {

    private static final String REALM_ID = "house";

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
    void whenAddRealmRole_givenInvalidName_thenThrowException(String invalid) {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> roles.addRealmRole(realm, invalid));
        });
    }

    @Test
    void whenAddRealmRole_givenExistingId_thenThrowException() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Arrange
            roles.addRealmRole(realm, "garden", "tree");
            // Act & Assert
            assertThatExceptionOfType(ModelDuplicateException.class)
                    .isThrownBy(() -> roles.addRealmRole(realm, "garden", "bush"));
        });
    }

    @Test
    void whenAddRealmRole_givenExistingName_thenThrowException() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Arrange
            roles.addRealmRole(realm, "garden");
            // Act & Assert
            assertThatExceptionOfType(ModelDuplicateException.class)
                    .isThrownBy(() -> roles.addRealmRole(realm, "garden"));
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenGetClientRole_givenInvalidParam_thenReturnNull(String invalid) {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Act
            var actual = roles.getClientRole(null, invalid);
            // Assert
            assertThat(actual).isNull();
        });
    }

    @Test
    void whenGetRealmRolesStream_givenNoRoles_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Act
            var actual = roles.getRealmRolesStream(realm);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenGetRealmRolesStream_givenRoles_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Arrange
            roles.addRealmRole(realm, "porch");
            roles.addRealmRole(realm, "garden");
            // Act
            var actual = roles.getRealmRolesStream(realm);
            // Assert
            assertThat(actual)
                    .hasSize(2)
                    .map(RoleModel::getName)
                    .containsExactly("garden", "porch");
        });
    }

    @Test
    void whenGetRolesStream_givenNoRoles_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Act
            var actual = roles.getRolesStream(realm, Stream.of(), null, null, null);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenGetRolesStream_givenRoles_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Arrange
            roles.addRealmRole(realm, "kitchen");
            roles.addRealmRole(realm, "attic");
            // Act
            var actual = roles.getRolesStream(realm, Stream.of("kitchen"), null, null, null);
            // Assert
            assertThat(actual)
                    .hasSize(1)
                    .map(RoleModel::getId)
                    .containsExactly("kitchen");
        });
    }

    @Test
    void whenGetRolesStream_givenSearchPattern_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Arrange
            roles.addRealmRole(realm, "no-match");// no-match > exclude
            roles.addRealmRole(realm, "patty");// partial-match > exclude
            roles.addRealmRole(realm, "abc-pattern");
            roles.addRealmRole(realm, "abc-pattern-xyz");
            roles.addRealmRole(realm, "pattern");
            roles.addRealmRole(realm, "pattern-xyz");
            // Act
            Stream<String> ids = Stream.of("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
            var actual = roles.getRolesStream(realm, ids, "pattern", null, null);
            // Assert
            assertThat(actual)
                    .hasSize(4)
                    .map(RoleModel::getId)
                    .containsExactlyInAnyOrder("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
        });
    }

    @Test
    void whenAddClientRole_givenClient_thenNameIsSet() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "dog");
            // Act
            var actual = session.roles().addClientRole(client, "pet");
            // Assert
            assertThat(actual.getName()).isEqualTo("pet");
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenAddClientRole_givenInvalidName_thenExceptionIsThrown(String invalid) {
        withRealm(REALM_ID, (session, realm) -> {
            // Act & Assert
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> session.roles().addClientRole(null, invalid));
        });
    }

    @Test
    void whenAddClientRole_givenExistingClient_thenExceptionIsThrown() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "dog");
            session.roles().addClientRole(client, "pet");
            // Act & Assert
            assertThatExceptionOfType(ModelDuplicateException.class)
                    .isThrownBy(() -> session.roles().addClientRole(client, "pet"));
        });
    }

    @Test
    void whenAddClientRole_givenExistingRole_thenExceptionIsThrown() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "dog");
            session.roles().addRealmRole(realm, "dog:pet");
            // Act & Assert
            assertThatExceptionOfType(ModelDuplicateException.class)
                    .isThrownBy(() -> session.roles().addClientRole(client, "pet"));
        });
    }

    @Test
    void whenAddClientRole_givenIdNull_thenIdIsSet() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "jar");
            // Act
            var actual = session.roles().addClientRole(client, "chef");
            // Assert
            assertThat(actual.getId()).isEqualTo("jar:chef");
        });
    }

    @Test
    void whenAddClientRole_givenId_thenIdIsSet() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "dog");
            // Act
            var actual = session.roles().addClientRole(client, "pet", "Pet");
            // Assert
            assertThat(actual.getId()).isEqualTo("dog:pet");
        });
    }

    @Test
    void whenGetClientRolesStream_givenNoRoles_thenReturnEmptyStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "cat");
            // Act
            var actual = session.roles().getClientRolesStream(client);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenGetClientRolesStream_givenRoles_thenReturnStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "cat");
            session.roles().addClientRole(client, "pet");
            session.roles().addClientRole(client, "furry");
            // Act
            var actual = session.roles().getClientRolesStream(client);
            // Assert
            assertThat(actual)
                    .hasSize(2)
                    .map(RoleModel::getName)
                    .containsExactly("furry", "pet");
        });
    }

    @Test
    void whenGetClientRolesStream_givenPagination_thenReturnStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "cat");
            session.roles().addClientRole(client, "0");
            session.roles().addClientRole(client, "1");
            session.roles().addClientRole(client, "2");
            session.roles().addClientRole(client, "3");
            // Act
            var actual = session.roles().getClientRolesStream(client, 1, 2);
            // Assert
            assertThat(actual)
                    .hasSize(2)
                    .map(RoleModel::getName)
                    .containsExactly("1", "2");
        });
    }

    @Test
    void whenRemoveRole_givenRealmRole_thenRoleIsRemoved() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var role = session.roles().addRealmRole(realm, "living-room");
            // Act
            session.roles().removeRole(role);
            // Assert
            RoleModel removedRole = session.roles().getRoleById(realm, "living-room");
            assertThat(removedRole).isNull();
        });
    }

    @Test
    void whenRemoveRole_givenClientRole_thenRoleIsRemoved() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "cat");
            var role = session.roles().addClientRole(client, "alive");
            // Act
            session.roles().removeRole(role);
            // Assert
            var removedRole = session.roles().getRoleById(realm, "alive");
            assertThat(removedRole).isNull();
        });
    }

    @Test
    void whenRemoveRoles_givenNoRealmsRoles_thenRolesAreEmpty() {
        withRealm(REALM_ID, (session, realm) -> {
            // Act
            session.roles().removeRoles(realm);
            // Assert
            var actual = session.roles().getRealmRolesStream(realm);
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenRemoveRoles_givenRealmRoles_thenRolesAreEmpty() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            session.roles().addRealmRole(realm, "bed");
            session.roles().addRealmRole(realm, "pillow");
            session.roles().addRealmRole(realm, "blanket");
            // Act
            session.roles().removeRoles(realm);
            // Assert
            var actual = session.roles().getRealmRolesStream(realm);
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenRemoveRoles_givenNoClientRoles_thenRolesAreEmpty() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "guest-room");
            // Act
            session.roles().removeRoles(client);
            // Assert
            var actual = client.getRolesStream();
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenRemoveRoles_givenClientRoles_thenRolesAreEmpty() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "guest-room");
            session.roles().addClientRole(client, "table");
            session.roles().addClientRole(client, "chair");
            // Act
            session.roles().removeRoles(client);
            // Assert
            var actual = client.getRolesStream();
            assertThat(actual).isEmpty();
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenGetRealmRole_givenInvalidName_thenReturnNull(String invalid) {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Act
            var actual = roles.getRealmRole(realm, invalid);
            // Assert
            assertThat(actual).isNull();
        });
    }

    @Test
    void whenGetRealmRole_givenNoRole_thenReturnNull() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Act
            var actual = roles.getRealmRole(realm, "abc");
            // Assert
            assertThat(actual).isNull();
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void whenGetRoleById_givenInvalidParam_thenReturnNull(String invalid) {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Act
            var actual = roles.getRoleById(realm, invalid);
            // Assert
            assertThat(actual).isNull();
        });
    }

    @Test
    void whenGetRoleById_givenExistingId_thenReturnRole() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            roles.addRealmRole(realm, "freezer");
            // Act
            var actual = roles.getRoleById(realm, "freezer");
            // Assert
            assertThat(actual.getName()).isEqualTo("freezer");
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void whenSearchForRolesStream_givenInvalidSearch_thenReturnEmptyStream(String invalid) {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            // Act
            var actual = roles.searchForRolesStream(realm, invalid, null, null);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchForRolesStream_givenSearchString_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::roles, (roles, realm) -> {
            roles.addRealmRole(realm, "patty"); // partial match -> exclude
            roles.addRealmRole(realm, "no-match"); // no match -> exclude
            roles.addRealmRole(realm, "abc-pattern");
            roles.addRealmRole(realm, "abc-pattern-xyz");
            roles.addRealmRole(realm, "pattern");
            roles.addRealmRole(realm, "pattern-xyz");
            // Act
            var actual = roles.searchForRolesStream(realm, "pattern", null, null);
            // Assert
            assertThat(actual)
                    .hasSize(4)
                    .map(RoleModel::getName)
                    .containsExactly("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
        });
    }

    @Test
    void whenSearchForClientRolesStream_givenSearchString_thenReturnStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "bathtub");

            session.roles().addClientRole(client, "patty"); // partial match -> exclude
            session.roles().addClientRole(client, "no-match"); // no match -> exclude
            session.roles().addClientRole(client, "abc-pattern");
            session.roles().addClientRole(client, "abc-pattern-xyz");
            session.roles().addClientRole(client, "pattern");
            session.roles().addClientRole(client, "pattern-xyz");
            // Act
            var actual = session.roles().searchForClientRolesStream(client, "pattern", null, null);
            // Assert
            assertThat(actual)
                    .hasSize(4)
                    .map(RoleModel::getName)
                    .containsExactly("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
        });
    }


    @Test
    void whenSearchForClientRolesStream_givenSearchNull_thenReturnEmptyStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Act
            var actual = session.roles().searchForClientRolesStream(realm, Stream.of(), null, null, null);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchForClientRolesStream_givenIds_thenReturnStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var ids = List.of(
                    "patty", // partial match -> exclude
                    "no-match", // no match -> exclude
                    "abc-pattern",
                    "abc-pattern-xyz",
                    "pattern",
                    "pattern-xyz");

            ids.forEach(id -> session.roles().addRealmRole(realm, id));
            // Act
            var actual = session.roles().searchForClientRolesStream(realm, ids.stream(), "pattern", null, null);
            // Assert
            assertThat(actual)
                    .hasSize(4)
                    .map(RoleModel::getName)
                    .containsExactly("abc-pattern", "abc-pattern-xyz", "pattern", "pattern-xyz");
        });
    }

    @Test
    void whenSearchForClientRolesStream_givenSearchNullAndExcludedIds_thenReturnEmptyStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Act
            var actual = session.roles().searchForClientRolesStream(realm, null, Stream.of(), null, null);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchForClientRolesStream_givenExcludedIds_thenReturnStream() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var ids = List.of(
                    "patty", // partial match -> exclude
                    "no-match", // no match -> exclude
                    "abc-pattern",
                    "abc-pattern-xyz",
                    "pattern",
                    "pattern-xyz");

            ids.forEach(id -> session.roles().addRealmRole(realm, id));
            // Act
            Stream<String> excludedIds = Stream.of("abc-pattern", "abc-pattern-xyz");
            var actual = session.roles().searchForClientRolesStream(realm, "pattern", excludedIds, null, null);
            // Assert
            assertThat(actual)
                    .hasSize(2)
                    .map(RoleModel::getName)
                    .containsExactly("pattern", "pattern-xyz");
        });
    }


}
