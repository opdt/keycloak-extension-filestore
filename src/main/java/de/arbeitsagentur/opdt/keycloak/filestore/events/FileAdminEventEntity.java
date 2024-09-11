package de.arbeitsagentur.opdt.keycloak.filestore.events;

import de.arbeitsagentur.opdt.keycloak.filestore.common.ExpirableEntity;
import org.keycloak.events.admin.OperationType;

public class FileAdminEventEntity implements ExpirableEntity {

  private String id;
  private Long expiration;
  private boolean isUpdated;
  private Long timestamp;
  private String realmId;
  private OperationType operationType;
  private String resourcePath;
  private String representation;
  private String error;
  private String resourceType;
  private String authRealmId;
  private String authClientId;
  private String authUserId;
  private String authIpAddress;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getExpiration() {
    return expiration;
  }

  public void setExpiration(Long expiration) {
    this.expiration = expiration;
  }

  public boolean isUpdated() {
    return isUpdated;
  }

  public void setUpdated(boolean updated) {
    isUpdated = updated;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getRealmId() {
    return realmId;
  }

  public void setRealmId(String realmId) {
    this.realmId = realmId;
  }

  public OperationType getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationType operationType) {
    this.operationType = operationType;
  }

  public String getResourcePath() {
    return resourcePath;
  }

  public void setResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  public String getRepresentation() {
    return representation;
  }

  public void setRepresentation(String representation) {
    this.representation = representation;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public String getAuthRealmId() {
    return authRealmId;
  }

  public void setAuthRealmId(String authRealmId) {
    this.authRealmId = authRealmId;
  }

  public String getAuthClientId() {
    return authClientId;
  }

  public void setAuthClientId(String authClientId) {
    this.authClientId = authClientId;
  }

  public String getAuthUserId() {
    return authUserId;
  }

  public void setAuthUserId(String authUserId) {
    this.authUserId = authUserId;
  }

  public String getAuthIpAddress() {
    return authIpAddress;
  }

  public void setAuthIpAddress(String authIpAddress) {
    this.authIpAddress = authIpAddress;
  }
}
