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
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RealmProviderFactory;
import org.keycloak.provider.InvalidationHandler;
import org.keycloak.provider.ProviderEvent;
import org.keycloak.provider.ProviderEventListener;

/**
 * !!: This is a nasty workaround.
 *
 * <p>Keycloak registers the {@link JpaRealmProviderFactory} as an listener to react to events that
 * are send for example by our {@link FileRealmProviderFactory#invalidate(KeycloakSession,
 * InvalidationHandler.InvalidableObjectType, Object...)}. The Jpa implementation which is not used
 * but still active cannot process the event properly, for which reason it is the best to just
 * remove the JpaRealmProviderFactory entirely. However since the class and its initialization lies
 * in the keycloak repository, we have to explicitly overwrite it's implementation here. We do that
 * by implementing the {@link RealmProviderFactory} interface, using the same provider id and
 * returning a higher priority for that empty implementation.
 */
@AutoService(RealmProviderFactory.class)
public class JpaRealmProviderFactory implements RealmProviderFactory, ProviderEventListener {

    private Runnable onClose;

    public static final String PROVIDER_ID = "jpa";
    public static final int PROVIDER_PRIORITY = 1;

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public RealmProvider create(KeycloakSession session) {
        return null;
    }

    @Override
    public void close() {}

    @Override
    public void onEvent(ProviderEvent event) {}

    @Override
    public int order() {
        return PROVIDER_PRIORITY + 1;
    }
}
