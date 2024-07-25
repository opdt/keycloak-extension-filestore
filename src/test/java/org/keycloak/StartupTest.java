package org.keycloak;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.testsuite.model.KeycloakModelTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StartupTest extends KeycloakModelTest {

    @Test
    void whenLaunching_givenFiles_thenLoadConfigFromFiles() {
        inCommittedTransaction(
                session -> {
                    Stream<RealmModel> realms = session.realms().getRealmsStream();
                    assertThat(realms)
                            .hasSize(1)
                            .map(RealmModel::getId)
                            .containsExactly("master");
                    RealmModel realm = session.realms().getRealm("master");
                    Stream<ClientModel> clients = session.clients().getClientsStream(realm);
                    Stream<ClientScopeModel> clientScopes = session.clientScopes().getClientScopesStream(realm);
                    Stream<RoleModel> roles = session.roles().getRealmRolesStream(realm);
                    Stream<GroupModel> groups = session.groups().getGroupsStream(realm);
                    assertThat(clients)
                            .map(ClientModel::getClientId)
                            .containsExactly("account", "admin-cli", "master-realm");
                    assertThat(clientScopes)
                            .map(ClientScopeModel::getName)
                            .containsExactly("acr", "address", "email", "microprofile-jwt", "offline_access", "phone", "profile", "role_list", "roles", "web-origins");
                    assertThat(roles)
                            .map(RoleModel::getId)
                            .containsExactly("admin", "create-realm", "default-roles-master", "offline_access", "uma_authorization");
                    assertThat(groups)
                            .map(GroupModel::getName)
                            .containsExactly("test-group");
                });
    }
}
