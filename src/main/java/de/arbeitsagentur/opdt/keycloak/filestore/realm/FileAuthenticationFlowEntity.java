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
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class FileAuthenticationFlowEntity implements AbstractEntity, UpdatableEntity {

  private String id;
  private boolean isUpdated = false;
  private String alias;
  private String description;
  private String providerId;
  private Boolean builtIn = false;
  private Boolean topLevel = false;

  static FileAuthenticationFlowEntity fromModel(AuthenticationFlowModel model) {
    FileAuthenticationFlowEntity entity = new FileAuthenticationFlowEntity();
    String id = model.getId() == null ? KeycloakModelUtils.generateId() : model.getId();
    entity.setId(id);
    entity.setAlias(model.getAlias());
    entity.setBuiltIn(model.isBuiltIn());
    entity.setDescription(model.getDescription());
    entity.setProviderId(model.getProviderId());
    entity.setTopLevel(model.isTopLevel());
    return entity;
  }

  static AuthenticationFlowModel toModel(FileAuthenticationFlowEntity entity) {
    AuthenticationFlowModel model = new AuthenticationFlowModel();
    model.setId(entity.getId());
    model.setAlias(entity.getAlias());
    Boolean builtIn = entity.isBuiltIn();
    model.setBuiltIn(builtIn == null ? false : builtIn);
    model.setDescription(entity.getDescription());
    model.setProviderId(entity.getProviderId());
    Boolean topLevel = entity.isTopLevel();
    model.setTopLevel(topLevel == null ? false : topLevel);
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

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getProviderId() {
    return this.providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public Boolean isBuiltIn() {
    return this.builtIn;
  }

  public void setBuiltIn(Boolean builtIn) {
    this.builtIn = builtIn;
  }

  public Boolean isTopLevel() {
    return this.topLevel;
  }

  public void setTopLevel(Boolean topLevel) {
    this.topLevel = topLevel;
  }

  public void setUpdated(boolean updated) {
    isUpdated = updated;
  }

  public Boolean getBuiltIn() {
    return builtIn;
  }

  public Boolean getTopLevel() {
    return topLevel;
  }
}
