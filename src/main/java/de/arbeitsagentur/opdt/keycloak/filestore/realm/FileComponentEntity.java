package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class FileComponentEntity implements AbstractEntity, UpdatableEntity {

  private String id;
  private boolean isUpdated = false;
  private String name;
  private String providerId;
  private String providerType;
  private String subType;
  private String parentId;
  private Map<String, Object> config;

  static FileComponentEntity fromModel(ComponentModel model) {
    FileComponentEntity entity = new FileComponentEntity();
    String id = model.getId() == null ? KeycloakModelUtils.generateId() : model.getId();
    entity.setId(id);
    entity.setName(model.getName());
    entity.setProviderId(model.getProviderId());
    entity.setProviderType(model.getProviderType());
    entity.setSubType(model.getSubType());
    entity.setParentId(model.getParentId());
    entity.convertFromMultivaluedConfig(model.getConfig());
    return entity;
  }

  static ComponentModel toModel(FileComponentEntity entity) {
    ComponentModel model = new ComponentModel();
    model.setId(entity.getId());
    model.setName(entity.getName());
    model.setProviderId(entity.getProviderId());
    model.setProviderType(entity.getProviderType());
    model.setSubType(entity.getSubType());
    model.setParentId(entity.getParentId());
    Map<String, List<String>> config = entity.convertToMultivaluedConfig();
    model.setConfig(config == null ? new MultivaluedHashMap<>() : new MultivaluedHashMap<>(config));
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

  public String getProviderId() {
    return this.providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public String getProviderType() {
    return this.providerType;
  }

  public void setProviderType(String providerType) {
    this.providerType = providerType;
  }

  public String getSubType() {
    return this.subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public String getParentId() {
    return this.parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  private Map<String, List<String>> convertToMultivaluedConfig() {
    Map<String, List<String>> config = new HashMap<>();
    for (Map.Entry e : this.config.entrySet()) {
      if (e.getValue() != null && e.getKey() != null) {
        config.put((String) e.getKey(), List.of(e.getValue().toString()));
      }
    }
    return config;
  }

  protected void convertFromMultivaluedConfig(Map<String, List<String>> config) {
    Map<String, Object> newConfig = new HashMap<>();
    for (String key : config.keySet()) {
      String singleVal = config.get(key).stream().findFirst().orElse(null);
      newConfig.put(key, singleVal);
    }
    this.config = newConfig;
  }

  public void setUpdated(boolean updated) {
    isUpdated = updated;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }

  public Map<String, Object> getConfig() {
    return config;
  }
}
