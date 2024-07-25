/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.models.map.events;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.component.AmphibianProviderFactory;
import org.keycloak.events.EventStoreProvider;
import org.keycloak.events.EventStoreProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.map.common.AbstractFileProviderFactory;
import org.keycloak.provider.InvalidationHandler;

import static org.keycloak.models.map.common.AbstractFileProviderFactory.MapProviderObjectType.REALM_BEFORE_REMOVE;
import static org.keycloak.models.map.common.AbstractFileProviderFactory.uniqueCounter;

@AutoService(EventStoreProviderFactory.class)
public class FileEventStoreProviderFactory implements AmphibianProviderFactory<EventStoreProvider>, EventStoreProviderFactory, InvalidationHandler {

    public static final String PROVIDER_ID = AbstractFileProviderFactory.PROVIDER_ID;

    protected final String uniqueKey = getClass().getName() + uniqueCounter.incrementAndGet();


    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public EventStoreProvider create(KeycloakSession session) {
        var provider = new FileEventStoreProvider(session);
        session.setAttribute(uniqueKey, provider);
        return provider;
    }

    @Override
    public void invalidate(KeycloakSession session, InvalidableObjectType type, Object... params) {
        if (type == REALM_BEFORE_REMOVE) {
            getInstance(session).clear((RealmModel) params[0]);
            getInstance(session).clearAdmin((RealmModel) params[0]);
        }
    }

    protected EventStoreProvider getInstance(KeycloakSession session) {
        FileEventStoreProvider existingProvider = session.getAttribute(uniqueKey, FileEventStoreProvider.class);
        if (existingProvider != null) {
            return existingProvider;
        } else {
            return create(session);
        }
    }

    @Override
    public void close() {
        AmphibianProviderFactory.super.close();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Event provider";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE; // Ensure this provider is used
    }
}
