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
package de.arbeitsagentur.opdt.keycloak.filestore.client;

import com.google.auto.service.AutoService;
import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.keycloak.models.*;
import org.keycloak.provider.InvalidationHandler;

/**
 * @author hmlnarik
 */
@AutoService(ClientProviderFactory.class)
public class FileClientProviderFactory
        extends AbstractFileProviderFactory<FileClientProvider, FileClientEntity, ClientModel>
        implements ClientProviderFactory<FileClientProvider>, InvalidationHandler {

    private final ConcurrentHashMap<String, ConcurrentMap<String, Long>> REGISTERED_NODES_STORE =
            new ConcurrentHashMap<>();

    public FileClientProviderFactory() {
        super(ClientModel.class, FileClientProvider.class);
    }

    @Override
    public FileClientProvider createNew(KeycloakSession session) {
        return new FileClientProvider(session, REGISTERED_NODES_STORE);
    }

    @Override
    public String getHelpText() {
        return "Client provider";
    }

    @Override
    public void invalidate(KeycloakSession session, InvalidableObjectType type, Object... params) {
        if (type == MapProviderObjectType.REALM_BEFORE_REMOVE) {
            create(session).preRemove((RealmModel) params[0]);
        } else if (type == MapProviderObjectType.ROLE_BEFORE_REMOVE) {
            create(session).preRemove((RealmModel) params[0], (RoleModel) params[1]);
        } else if (type == MapProviderObjectType.CLIENT_AFTER_REMOVE) {
            session.getKeycloakSessionFactory().publish(new ClientModel.ClientRemovedEvent() {
                @Override
                public ClientModel getClient() {
                    return (ClientModel) params[0];
                }

                @Override
                public KeycloakSession getKeycloakSession() {
                    return session;
                }
            });
        }
    }
}
