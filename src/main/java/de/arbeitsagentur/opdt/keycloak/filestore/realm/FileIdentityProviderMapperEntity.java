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

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import java.util.HashMap;
import java.util.Map;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class FileIdentityProviderMapperEntity implements AbstractEntity, UpdatableEntity {

  private String id;
  private boolean isUpdated = false;
  private String name;
  private String identityProviderAlias;
  private String identityProviderMapper;
  private Map<String, String> config;

  static FileIdentityProviderMapperEntity fromModel(IdentityProviderMapperModel model) {
    if (model == null) return null;
    FileIdentityProviderMapperEntity entity = new FileIdentityProviderMapperEntity();
    String id = model.getId() == null ? KeycloakModelUtils.generateId() : model.getId();
    entity.setId(id);
    entity.setName(model.getName());
    entity.setIdentityProviderAlias(model.getIdentityProviderAlias());
    entity.setIdentityProviderMapper(model.getIdentityProviderMapper());
    entity.setConfig(model.getConfig());
    return entity;
  }

  static IdentityProviderMapperModel toModel(FileIdentityProviderMapperEntity entity) {
    if (entity == null) return null;
    IdentityProviderMapperModel model = new IdentityProviderMapperModel();
    model.setId(entity.getId());
    model.setName(entity.getName());
    model.setIdentityProviderAlias(entity.getIdentityProviderAlias());
    model.setIdentityProviderMapper(entity.getIdentityProviderMapper());
    Map<String, String> config = entity.getConfig();
    model.setConfig(config == null ? new HashMap<>() : new HashMap<>(config));
    return model;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public boolean isUpdated() {
    return this.isUpdated;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIdentityProviderAlias() {
    return this.identityProviderAlias;
  }

  public void setIdentityProviderAlias(String identityProviderAlias) {
    this.identityProviderAlias = identityProviderAlias;
  }

  public String getIdentityProviderMapper() {
    return this.identityProviderMapper;
  }

  public void setIdentityProviderMapper(String identityProviderMapper) {
    this.identityProviderMapper = identityProviderMapper;
  }

  public Map<String, String> getConfig() {
    return this.config;
  }

  public void setConfig(Map<String, String> config) {
    this.config = config;
  }

  public void setUpdated(boolean updated) {
    isUpdated = updated;
  }
}
