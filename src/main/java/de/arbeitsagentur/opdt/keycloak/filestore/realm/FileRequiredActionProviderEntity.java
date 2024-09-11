package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import java.util.HashMap;
import java.util.Map;
import org.keycloak.models.RequiredActionProviderModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class FileRequiredActionProviderEntity implements AbstractEntity, UpdatableEntity {

  private String id;
  private boolean isUpdated = false;
  private String alias;
  private String name;
  private String providerId;
  private Integer priority;
  private Boolean enabled = false;
  private Boolean defaultAction = false;
  private Map<String, String> config;

  static FileRequiredActionProviderEntity fromModel(RequiredActionProviderModel model) {
    if (model == null) return null;
    FileRequiredActionProviderEntity entity = new FileRequiredActionProviderEntity();
    String id = model.getId() == null ? KeycloakModelUtils.generateId() : model.getId();
    entity.setId(id);
    entity.setAlias(model.getAlias());
    entity.setName(model.getName());
    entity.setProviderId(model.getProviderId());
    entity.setPriority(model.getPriority());
    entity.setEnabled(model.isEnabled());
    entity.setDefaultAction(model.isDefaultAction());
    entity.setConfig(model.getConfig());
    return entity;
  }

  static RequiredActionProviderModel toModel(FileRequiredActionProviderEntity entity) {
    if (entity == null) return null;
    RequiredActionProviderModel model = new RequiredActionProviderModel();
    model.setId(entity.getId());
    model.setAlias(entity.getAlias());
    model.setName(entity.getName());
    model.setProviderId(entity.getProviderId());
    Integer priority = entity.getPriority();
    model.setPriority(priority == null ? 0 : priority);
    Boolean enabled = entity.isEnabled();
    model.setEnabled(enabled == null ? false : enabled);
    Boolean defaultAction = entity.isDefaultAction();
    model.setDefaultAction(defaultAction == null ? false : defaultAction);
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

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProviderId() {
    return this.providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public Integer getPriority() {
    return this.priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Boolean isDefaultAction() {
    return this.defaultAction;
  }

  public void setDefaultAction(Boolean defaultAction) {
    this.defaultAction = defaultAction;
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

  public Boolean getEnabled() {
    return enabled;
  }

  public Boolean getDefaultAction() {
    return defaultAction;
  }
}
