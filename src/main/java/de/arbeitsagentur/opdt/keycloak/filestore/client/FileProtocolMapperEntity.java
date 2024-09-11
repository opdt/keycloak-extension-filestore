package de.arbeitsagentur.opdt.keycloak.filestore.client;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import java.util.HashMap;
import java.util.Map;

public class FileProtocolMapperEntity implements AbstractEntity, UpdatableEntity {

  public String name;
  public String protocolMapper;
  public Map<String, String> config = new HashMap<>();
  public String id;
  public boolean isUpdated;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProtocolMapper() {
    return this.protocolMapper;
  }

  public void setProtocolMapper(String protocolMapper) {
    this.protocolMapper = protocolMapper;
  }

  public Map<String, String> getConfig() {
    return this.config;
  }

  public void setConfig(Map<String, String> config) {
    this.config = config;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean isUpdated() {
    return this.isUpdated;
  }
}
