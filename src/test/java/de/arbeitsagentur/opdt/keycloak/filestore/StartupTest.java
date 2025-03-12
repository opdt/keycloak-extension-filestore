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

package de.arbeitsagentur.opdt.keycloak.filestore;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.keycloak.models.*;

class StartupTest extends KeycloakModelTest {

    @Test
    void whenLaunching_givenFiles_thenLoadConfigFromFiles() {
        inCommittedTransaction(session -> {
            Stream<RealmModel> realms = session.realms().getRealmsStream();
            assertThat(realms).hasSize(1).map(RealmModel::getId).containsExactly("master");
            RealmModel realm = session.realms().getRealm("master");
            Stream<ClientModel> clients = session.clients().getClientsStream(realm);
            Stream<ClientScopeModel> clientScopes = session.clientScopes().getClientScopesStream(realm);
            Stream<RoleModel> roles = session.roles().getRealmRolesStream(realm);
            Stream<GroupModel> groups = session.groups().getGroupsStream(realm);
            assertThat(clients).map(ClientModel::getClientId).containsExactly("account", "admin-cli", "master-realm");
            assertThat(clientScopes)
                    .map(ClientScopeModel::getName)
                    .containsExactly(
                            "acr",
                            "address",
                            "email",
                            "microprofile-jwt",
                            "offline_access",
                            "phone",
                            "profile",
                            "role_list",
                            "roles",
                            "web-origins");
            assertThat(roles)
                    .map(RoleModel::getId)
                    .containsExactly(
                            "admin", "create-realm", "default-roles-master", "offline_access", "uma_authorization");
            assertThat(groups).map(GroupModel::getName).containsExactly("test-group");
        });
    }
}
