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

import de.arbeitsagentur.opdt.keycloak.filestore.client.FileProtocolMapperEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import java.util.*;

public class FileClientScopeEntity implements AbstractEntity, UpdatableEntity {

    private String name;
    private String description;
    private String protocol;
    private String realmId;
    private List<FileProtocolMapperEntity> protocolMappers = new ArrayList<>();
    private List<String> scopeMappings = new ArrayList<>();
    private String id;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean isUpdated = false;

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getRealmId() {
        return this.realmId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
        FileClientScopeStore.update(this);
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
        FileClientScopeStore.update(this);
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public Optional<FileProtocolMapperEntity> getProtocolMapper(String id) {
        return this.protocolMappers.stream()
                .filter(mapper -> mapper.getId().equals(id))
                .findFirst();
    }

    public List<FileProtocolMapperEntity> getProtocolMappers() {
        return this.protocolMappers;
    }

    public void setProtocolMappers(List<FileProtocolMapperEntity> protocolMappers) {
        this.protocolMappers = protocolMappers;
        FileClientScopeStore.update(this);
    }

    public void addProtocolMapper(FileProtocolMapperEntity mapping) {
        this.protocolMappers.add(mapping);
        FileClientScopeStore.update(this);
    }

    public void removeProtocolMapper(String id) {
        this.protocolMappers.stream()
                .filter(mapper -> mapper.getId().equals(id))
                .findFirst()
                .ifPresent(this.protocolMappers::remove);
        FileClientScopeStore.update(this);
    }

    public void addScopeMapping(String id) {
        this.scopeMappings.add(id);
        FileClientScopeStore.update(this);
    }

    public void setScopeMappings(List<String> scopeMappings) {
        this.scopeMappings = scopeMappings;
        FileClientScopeStore.update(this);
    }

    public void removeScopeMapping(String id) {
        this.scopeMappings.remove(id);
        FileClientScopeStore.update(this);
    }

    public List<String> getScopeMappings() {
        return this.scopeMappings;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Map<String, List<String>> getMultivaluedAttributes() {
        return this.attributes.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), List.of(entry.getValue().toString())))
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        FileClientScopeStore.update(this);
    }

    public List<String> getAttribute(String name) {
        return Optional.ofNullable(this.attributes.get(name))
                .map(Object::toString)
                .map(List::of)
                .orElseGet(Collections::emptyList);
    }

    public void setAttribute(String name, List<String> value) {
        this.attributes.put(name, value.get(0));
        FileClientScopeStore.update(this);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
        FileClientScopeStore.update(this);
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }
}
