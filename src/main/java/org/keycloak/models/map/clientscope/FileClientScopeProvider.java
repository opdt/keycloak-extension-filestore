/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.models.map.clientscope;

import org.jboss.logging.Logger;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.keycloak.common.util.StackUtil.getShortStackTrace;
import static org.keycloak.models.map.common.AbstractFileProviderFactory.MapProviderObjectType.CLIENT_SCOPE_AFTER_REMOVE;
import static org.keycloak.models.map.common.AbstractFileProviderFactory.MapProviderObjectType.CLIENT_SCOPE_BEFORE_REMOVE;

public class FileClientScopeProvider implements ClientScopeProvider {

    private static final Logger LOG = Logger.getLogger(FileClientScopeProvider.class);
    private final KeycloakSession session;

    public FileClientScopeProvider(KeycloakSession session) {
        this.session = session;
    }

    private Function<FileClientScopeEntity, ClientScopeModel> entityToAdapterFunc(RealmModel realm) {
        return origEntity -> new FileClientScopeAdapter(session, realm, origEntity);
    }

    private boolean isEntityPartOfRealm(RealmModel realm, FileClientScopeEntity entity) {
        return Objects.equals(realm.getId(), entity.getRealmId());
    }

    @Override
    public Stream<ClientScopeModel> getClientScopesStream(RealmModel realm) {
        return FileClientScopeStore.readAll().stream()
                .filter(clientScope -> realm.getId().equals(clientScope.getRealmId()))
                .map(entityToAdapterFunc(realm))
                .sorted(Comparator.comparing(ClientScopeModel::getName));
    }

    @Override
    public ClientScopeModel addClientScope(RealmModel realm, String id, String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        if (FileClientScopeStore.exists(id, realm.getId())) {
            throw new ModelDuplicateException("Client scope exists: " + id);
        }

        boolean doesNameAlreadyExists = FileClientScopeStore.readAll().stream()
                .filter(clientScope -> realm.getId().equals(clientScope.getRealmId()))
                .filter(clientScope -> name.equals(clientScope.getName()))
                .map(entityToAdapterFunc(realm))
                .findAny()
                .isPresent();

        if (doesNameAlreadyExists) {
            throw new ModelDuplicateException("Client scope with name '" + name + "' in realm " + realm.getName());
        }

        LOG.tracef("addClientScope(%s, %s, %s)%s", realm, id, name, getShortStackTrace());
        FileClientScopeEntity entity = new FileClientScopeEntity();
        String newId = id != null ? id : name;
        entity.setId(newId);
        entity.setRealmId(realm.getId());
        entity.setName(KeycloakModelUtils.convertClientScopeName(name));

        FileClientScopeStore.update(entity);
        return entityToAdapterFunc(realm).apply(entity);
    }

    @Override
    public boolean removeClientScope(RealmModel realm, String id) {
        if (id == null) return false;

        ClientScopeModel clientScope = getClientScopeById(realm, id);
        if (clientScope == null) return false;

        session.invalidate(CLIENT_SCOPE_BEFORE_REMOVE, realm, clientScope);
        FileClientScopeStore.deleteById(id, realm.getId());
        session.invalidate(CLIENT_SCOPE_AFTER_REMOVE, clientScope);
        return true;
    }

    @Override
    public void removeClientScopes(RealmModel realm) {
        LOG.tracef("removeClients(%s)%s", realm, getShortStackTrace());
        getClientScopesStream(realm)
                .map(ClientScopeModel::getId)
                .collect(Collectors.toSet())  // This is necessary to read out all the client IDs before removing the clients
                .forEach(id -> removeClientScope(realm, id));
    }

    @Override
    public ClientScopeModel getClientScopeById(RealmModel realm, String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        LOG.tracef("getClientScopeById(%s, %s)%s", realm, id, getShortStackTrace());
        FileClientScopeEntity entity = FileClientScopeStore.read(id, realm.getId());
        if (entity != null && isEntityPartOfRealm(realm, entity)) {
            return entityToAdapterFunc(realm).apply(entity);
        } else {
            return null;
        }
    }

    public void preRemove(RealmModel realm) {
        LOG.tracef("preRemove(%s)%s", realm, getShortStackTrace());
        FileClientScopeStore.deleteByRealmId(realm.getId());
    }

    @Override
    public void close() {
    }
}
