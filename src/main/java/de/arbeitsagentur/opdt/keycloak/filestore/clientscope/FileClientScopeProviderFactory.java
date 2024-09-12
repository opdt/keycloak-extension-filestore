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

import static de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory.MapProviderObjectType.*;

import com.google.auto.service.AutoService;
import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.ClientScopeProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.InvalidationHandler;

@AutoService(ClientScopeProviderFactory.class)
public class FileClientScopeProviderFactory
    extends AbstractFileProviderFactory<
        FileClientScopeProvider, FileClientScopeEntity, ClientScopeModel>
    implements ClientScopeProviderFactory<FileClientScopeProvider>, InvalidationHandler {

  public FileClientScopeProviderFactory() {
    super(ClientScopeModel.class, FileClientScopeProvider.class);
  }

  @Override
  public FileClientScopeProvider createNew(KeycloakSession session) {
    return new FileClientScopeProvider(session);
  }

  @Override
  public String getHelpText() {
    return "Client scope provider";
  }

  @Override
  public void invalidate(KeycloakSession session, InvalidableObjectType type, Object... params) {
    if (type == REALM_BEFORE_REMOVE) {
      create(session).preRemove((RealmModel) params[0]);
    } else if (type == CLIENT_SCOPE_BEFORE_REMOVE) {
      ((RealmModel) params[0]).removeDefaultClientScope((ClientScopeModel) params[1]);
    } else if (type == CLIENT_SCOPE_AFTER_REMOVE) {
      session
          .getKeycloakSessionFactory()
          .publish(
              new ClientScopeModel.ClientScopeRemovedEvent() {
                @Override
                public ClientScopeModel getClientScope() {
                  return (ClientScopeModel) params[0];
                }

                @Override
                public KeycloakSession getKeycloakSession() {
                  return session;
                }
              });
    }
  }
}
