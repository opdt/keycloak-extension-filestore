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
