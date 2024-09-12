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
package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import com.google.auto.service.AutoService;
import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProviderFactory;
import org.keycloak.provider.InvalidationHandler;

@AutoService(RealmProviderFactory.class)
public class FileRealmProviderFactory
    extends AbstractFileProviderFactory<FileRealmProvider, FileRealmEntity, RealmModel>
    implements RealmProviderFactory<FileRealmProvider>, InvalidationHandler {

  public FileRealmProviderFactory() {
    super(RealmModel.class, FileRealmProvider.class);
  }

  @Override
  public FileRealmProvider createNew(KeycloakSession session) {
    return new FileRealmProvider(session);
  }

  @Override
  public String getHelpText() {
    return "Realm provider";
  }

  @Override
  public void invalidate(KeycloakSession session, InvalidableObjectType type, Object... params) {
    if (type == MapProviderObjectType.REALM_AFTER_REMOVE) {
      session
          .getKeycloakSessionFactory()
          .publish(
              new RealmModel.RealmRemovedEvent() {
                @Override
                public RealmModel getRealm() {
                  return (RealmModel) params[0];
                }

                @Override
                public KeycloakSession getKeycloakSession() {
                  return session;
                }
              });
    }
  }
}
