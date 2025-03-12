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

package de.arbeitsagentur.opdt.keycloak.filestore.compat;

import com.google.auto.service.AutoService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.SingleUseObjectProvider;
import org.keycloak.models.SingleUseObjectProviderFactory;
import org.keycloak.provider.Provider;

@AutoService(SingleUseObjectProviderFactory.class)
public class TestSingleUseObjectProviderFactory implements SingleUseObjectProviderFactory, SingleUseObjectProvider {
    private Map<String, Map<String, String>> store = new ConcurrentHashMap<>();

    @Override
    public void put(String key, long lifespanSeconds, Map<String, String> notes) {
        if (notes == null) {
            store.remove(key);
        } else {
            store.put(key, notes);
        }
    }

    @Override
    public Map<String, String> get(String key) {
        return store.get(key);
    }

    @Override
    public Map<String, String> remove(String key) {
        return store.remove(key);
    }

    @Override
    public boolean replace(String key, Map<String, String> notes) {
        store.replace(key, notes);
        return true;
    }

    @Override
    public boolean putIfAbsent(String key, long lifespanInSeconds) {
        if (!store.containsKey(key)) {
            store.putIfAbsent(key, new HashMap<>());
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(String key) {
        return store.containsKey(key);
    }

    @Override
    public Provider create(KeycloakSession session) {
        return new TestSingleUseObjectProviderFactory();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "test";
    }
}
