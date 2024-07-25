package org.keycloak.models.map.client;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.testsuite.model.KeycloakModelTest;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class FileClientProviderTest extends KeycloakModelTest {

    private static final String REALM_ID = "capital";

    @Override
    protected void createEnvironment(KeycloakSession s) {
        s.realms().createRealm(REALM_ID);
    }

    @Override
    protected void cleanEnvironment(KeycloakSession s) {
        s.realms().removeRealm(REALM_ID);
    }

    @Test
    void whenGetClientsStream_givenNoClients_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            Stream<ClientModel> actual = clients.getClientsStream(realm);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void whenGetClientById_givenUnknownId_thenReturnNull(String unknownId) {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            ClientModel actual = clients.getClientById(realm, unknownId);
            // Assert
            assertThat(actual).isNull();
        });
    }

    @Test
    void whenGetClientById_givenClient_thenReturnNotNull() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Nassau");
            // Act
            ClientModel actual = clients.getClientById(realm, "Nassau");
            // Assert
            assertThat(actual).isNotNull();
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void whenGetClientByClientId_givenUnknownClientId_thenReturnNull(String unknownClientId) {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            ClientModel actual = clients.getClientByClientId(realm, unknownClientId);
            // Assert
            assertThat(actual).isNull();
        });
    }

    @Test
    void whenGetClientByClientId_givenExistingClientId_thenReturnClient() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "London");
            // Act
            ClientModel actual = clients.getClientByClientId(realm, "London");
            // Assert
            assertThat(actual.getClientId()).isEqualTo("London");
        });
    }

    @Test
    void whenGetClientsStream_givenNoClient_thenStreamIsEmpty() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            Stream<ClientModel> actual = clients.getClientsStream(realm);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenGetClientsStream_givenClients_thenReturnStreamWithClients() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "London");
            clients.addClient(realm, "Dublin");
            clients.addClient(realm, "Paris");
            // Act
            Stream<ClientModel> actual = clients.getClientsStream(realm);
            // Assert
            assertThat(actual)
                    .hasSize(3)
                    .map(ClientModel::getClientId)
                    .containsExactlyInAnyOrder("London", "Dublin", "Paris");
        });
    }

    @Test
    void whenGetClientsStreamWithPagination_givenClients_thenReturnPaginatedStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            // Note: the list will be sorted alphabetically to get a consistent pagination
            clients.addClient(realm, "Amsterdam");
            clients.addClient(realm, "Berlin");
            clients.addClient(realm, "Dublin"); // <-- idx=2
            clients.addClient(realm, "London");
            clients.addClient(realm, "Oslo");  // <-- maxResults=3
            clients.addClient(realm, "Stockholm");
            // Act
            Stream<ClientModel> actual = clients.getClientsStream(realm, 2, 3);
            // Assert
            assertThat(actual)
                    .hasSize(3)
                    .map(ClientModel::getClientId)
                    .containsExactlyInAnyOrder("Dublin", "London", "Oslo");
        });
    }

    @Test
    void whenAddClient_givenNull_thenIllegalArgumentExceptionIsThrown() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act & Assert
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> clients.addClient(realm, null, null));
        });
    }

    @Test
    void whenAddClient_givenExistingClient_thenThrowException() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "warsaw", "Hamburg");
            // Act & Assert
            assertThatExceptionOfType(ModelDuplicateException.class)
                    .isThrownBy(() -> clients.addClient(realm, null, "Hamburg"));
        });
    }

    @Test
    void whenGetAlwaysDisplayInConsoleClientsStream_givenNoClients_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            Stream<ClientModel> actual = clients.getAlwaysDisplayInConsoleClientsStream(realm);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenGetAlwaysDisplayInConsoleClientsStream_givenNoMatchingClients_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Windhoek").setAlwaysDisplayInConsole(false); // will be excluded
            // Act
            Stream<ClientModel> actual = clients.getAlwaysDisplayInConsoleClientsStream(realm);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenGetAlwaysDisplayInConsoleClientsStream_givenClients_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Zagreb").setAlwaysDisplayInConsole(true);
            clients.addClient(realm, "Windhoek").setAlwaysDisplayInConsole(false); // will be excluded
            clients.addClient(realm, "Amsterdam").setAlwaysDisplayInConsole(true);
            // Act
            Stream<ClientModel> actual = clients.getAlwaysDisplayInConsoleClientsStream(realm);
            // Assert
            assertThat(actual)
                    .hasSize(2)
                    .map(ClientModel::getClientId)
                    .containsExactly("Amsterdam", "Zagreb");
        });
    }

    @Test
    void whenGetClientsCount_givenNoClients_thenReturnZero() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            long actual = clients.getClientsCount(realm);
            // Assert
            assertThat(actual).isZero();
        });
    }

    @Test
    void whenGetClientsCount_givenClients_thenReturnCount() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Vienna");
            clients.addClient(realm, "Rome");
            clients.addClient(realm, "Oslo");
            // Act
            long actual = clients.getClientsCount(realm);
            // Assert
            assertThat(actual).isEqualTo(3);
        });
    }

    @Test
    void whenRemoveClients_givenNoClients_thenNoExceptionIsThrown() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act & Assert
            assertThatNoException()
                    .isThrownBy(() -> clients.removeClients(realm));
        });
    }

    @Test
    void whenRemoveClients_givenSingleClient_thenClientIsRemoved() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Nairobi");
            // Act
            clients.removeClients(realm);
            // Assert
            var actual = clients.getClientsStream(realm);
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenRemoveClients_givenMultipleClients_thenClientIsRemoved() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Madrid");
            clients.addClient(realm, "Lisbon");
            clients.addClient(realm, "Lima");
            // Act
            clients.removeClients(realm);
            // Assert
            var actual = clients.getClientsStream(realm);
            assertThat(actual).isEmpty();
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void whenRemoveClient_givenNull_thenReturnFalse(String invalid) {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            var actual = clients.removeClient(realm, invalid);
            // Assert
            assertThat(actual).isFalse();
        });
    }

    @Test
    void whenRemoveClient_givenClient_thenReturnTrue() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Jakarta");
            // Act
            var actual = clients.removeClient(realm, "Jakarta");
            // Assert
            assertThat(actual).isTrue();
        });
    }

    @Test
    void whenSearchClientsByClientIdStream_givenNoClients_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Toronto"); // no match -> exclude
            clients.addClient(realm, "patty"); // partial match -> exclude
            // Act
            Stream<ClientModel> actual = clients.searchClientsByClientIdStream(realm, "pattern", null, null);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchClientsByClientIdStream_givenNullParam_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            Stream<ClientModel> actual = clients.searchClientsByClientIdStream(realm, null, null, null);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchClientsByClientIdStream_givenMatchingClients_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Toronto"); // no match -> exclude
            clients.addClient(realm, "patty"); // partial match -> exclude
            clients.addClient(realm, "abc pattern"); // end
            clients.addClient(realm, "abc pattern xyz"); // middle
            clients.addClient(realm, "pattern"); // exact
            clients.addClient(realm, "pattern xyz"); // start
            // Act
            Stream<ClientModel> actual = clients.searchClientsByClientIdStream(realm, "pattern", null, null);
            // Assert
            assertThat(actual).hasSize(4)
                    .map(ClientModel::getClientId)
                    .containsExactly("abc pattern", "abc pattern xyz", "pattern", "pattern xyz");
        });
    }

    @Test
    void whenSearchClientsByClientIdStream_givenResultLimits_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "0 pattern");
            clients.addClient(realm, "1 pattern"); // start, firstResult = 1
            clients.addClient(realm, "2 pattern"); // end, maxResults = 2
            clients.addClient(realm, "3 pattern");
            // Act
            Stream<ClientModel> actual = clients.searchClientsByClientIdStream(realm, "pattern", 1, 2);
            // Assert
            assertThat(actual).hasSize(2)
                    .map(ClientModel::getClientId)
                    .containsExactly("1 pattern", "2 pattern");
        });
    }

    @Test
    void whenSearchClientsByClientIdStream_givenOutOfBoundLimits_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "0 pattern");
            // Act
            Stream<ClientModel> actual = clients.searchClientsByClientIdStream(realm, "pattern", 5, 10);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchClientsByAttributes_givenNoClients_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            Stream<ClientModel> actual = clients.searchClientsByAttributes(realm, Map.of(), null, null);
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchClientsByAttributes_givenNoMatch_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Vilnius").setAttribute("no-match", "val");
            // Act
            Stream<ClientModel> actual = clients.searchClientsByAttributes(realm, Map.of("match", "val"), null, null);
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenSearchClientsByAttributes_givenClients_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "Vilnius").setAttribute("no-match", "val");
            clients.addClient(realm, "Zagreb").setAttribute("match", "val");
            clients.addClient(realm, "Albuquerque").setAttribute("match", "val");
            // Act
            Stream<ClientModel> actual = clients.searchClientsByAttributes(realm, Map.of("match", "val"), null, null);
            // Assert
            assertThat(actual)
                    .hasSize(2)
                    .map(ClientModel::getClientId)
                    .containsExactly("Albuquerque", "Zagreb");
        });
    }

    @Test
    void whenSearchClientsByAttributes_givenClientWithMultipleAttrs_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            var c = clients.addClient(realm, "Vilnius");
            c.setAttribute("no-match", "val");
            c.setAttribute("match", "val");
            // Act
            Stream<ClientModel> actual = clients.searchClientsByAttributes(realm, Map.of("match", "val"), null, null);
            // Assert
            assertThat(actual)
                    .hasSize(1)
                    .map(ClientModel::getClientId)
                    .containsExactly("Vilnius");
        });
    }

    @Test
    void whenSearchClientsByAttributes_givenMultipleSearchAttrs_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Arrange
            clients.addClient(realm, "NO MATCH").setAttribute("match-1", "val");
            var c = clients.addClient(realm, "Tokyo");
            c.setAttribute("match-1", "val");
            c.setAttribute("match-2", "val");
            // Act
            var searchMap = Map.of("match-1", "val", "match-2", "val");
            Stream<ClientModel> actual = clients.searchClientsByAttributes(realm, searchMap, null, null);
            // Assert
            assertThat(actual)
                    .hasSize(1)
                    .map(ClientModel::getClientId)
                    .containsExactly("Tokyo");
        });
    }

    @Test
    void whenAddClientScopes_givenClientScopes_thenNoExceptionIsThrown() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var client = session.clients().addClient(realm, "London");
            client.setProtocol("same protocol");
            var cs1 = session.clientScopes().addClientScope(realm, "clientScope-with-same-protocol");
            cs1.setProtocol("same protocol");
            var cs2 = session.clientScopes().addClientScope(realm, "clientScope-with-unknown-protocol");
            cs2.setProtocol("unknown protocol");
            // Act
            session.clients().addClientScopes(realm, client, Set.of(cs1, cs2), false);
            // Assert
            Map<String, ClientScopeModel> actual = session.clients().getClientScopes(realm, client, false);
            assertThat(actual)
                    .hasSize(1)
                    .containsKeys("clientScope-with-same-protocol");
        });
    }

    @Test
    void whenRemoveClientScope_givenNull_thenNoExceptionIsThrown() {
        withRealm(REALM_ID, (session, realm) -> {
            assertThatNoException()
                    .isThrownBy(() -> session.clients().removeClientScope(realm, null, null));
        });
    }

    @Test
    void whenRemoveClientScope_givenNoClientScope_thenNoExceptionIsThrown() {
        withRealm(REALM_ID, (session, realm) -> {
            var c = session.clients().addClient(realm, "Seoul");
            assertThatNoException()
                    .isThrownBy(() -> session.clients().removeClientScope(realm, c, null));
        });
    }

    @Test
    void whenRemoveClientScope_givenClientScope_thenClientScopeIsRemoved() {
        withRealm(REALM_ID, (session, realm) -> {
            // Arrange
            var c = session.clients().addClient(realm, "Seoul");
            var cs = session.clientScopes().addClientScope(realm, "suburb");
            c.addClientScope(cs, false);
            // Act
            session.clients().removeClientScope(realm, c, cs);
            // Assert
            assertThat(session.clients().getClientScopes(realm, c, false)).isEmpty();
        });
    }

    @Test
    void whenGetAllRedirectUrisOfEnabledClients_givenNoUris_thenReturnEmptyMap() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clients, (clients, realm) -> {
            // Act
            var actual = clients.getAllRedirectUrisOfEnabledClients(realm);
            // Assert
            assertThat(actual).isEmpty();
        });
    }
}
