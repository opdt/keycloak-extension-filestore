/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
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
package de.arbeitsagentur.opdt.keycloak.filestore.role;

import com.google.auto.service.AutoService;
import org.keycloak.models.*;
import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory;
import org.keycloak.provider.InvalidationHandler;

import static de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory.MapProviderObjectType.*;

@AutoService(RoleProviderFactory.class)
public class FileRoleProviderFactory extends AbstractFileProviderFactory<FileRoleProvider, FileRoleEntity, RoleModel> implements RoleProviderFactory<FileRoleProvider>, InvalidationHandler {

    public FileRoleProviderFactory() {
        super(RoleModel.class, FileRoleProvider.class);
    }

    @Override
    public FileRoleProvider createNew(KeycloakSession session) {
        return new FileRoleProvider(session);
    }

    @Override
    public String getHelpText() {
        return "Role provider";
    }

    @Override
    public void invalidate(KeycloakSession session, InvalidableObjectType type, Object... params) {
        if (type == REALM_BEFORE_REMOVE) {
            create(session).preRemove((RealmModel) params[0]);
        } else if (type == CLIENT_BEFORE_REMOVE) {
            create(session).removeRoles((ClientModel) params[1]);
        } else if (type == ROLE_BEFORE_REMOVE) {
            create(session).preRemove((RealmModel) params[0], (RoleModel) params[1]);
        } else if (type == ROLE_AFTER_REMOVE) {
            session.getKeycloakSessionFactory().publish(new RoleContainerModel.RoleRemovedEvent() {
                @Override
                public RoleModel getRole() {
                    return (RoleModel) params[1];
                }

                @Override
                public KeycloakSession getKeycloakSession() {
                    return session;
                }
            });
        }
    }
}
