package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.TimeAdapter;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import org.keycloak.common.util.Time;
import org.keycloak.models.ClientInitialAccessModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class FileClientInitialAccessEntity implements AbstractEntity, UpdatableEntity {

  private String id;
  private Long expiration;
  private boolean isUpdated;
  private Long timestamp;
  private Integer count;
  private Integer remainingCount;

  static FileClientInitialAccessEntity createEntity(int expiration, int count) {
    long currentTime = Time.currentTimeMillis();
    FileClientInitialAccessEntity entity = new FileClientInitialAccessEntity();
    entity.setId(KeycloakModelUtils.generateId());
    entity.setTimestamp(currentTime);
    entity.setExpiration(
        expiration == 0 ? null : currentTime + TimeAdapter.fromSecondsToMilliseconds(expiration));
    entity.setCount(count);
    entity.setRemainingCount(count);
    return entity;
  }

  static ClientInitialAccessModel toModel(FileClientInitialAccessEntity entity) {
    if (entity == null) return null;
    ClientInitialAccessModel model = new ClientInitialAccessModel();
    model.setId(entity.getId());
    Long timestampSeconds = TimeAdapter.fromMilliSecondsToSeconds(entity.getTimestamp());
    model.setTimestamp(
        timestampSeconds == null
            ? 0
            : TimeAdapter.fromLongWithTimeInSecondsToIntegerWithTimeInSeconds(timestampSeconds));
    Long expirationSeconds = TimeAdapter.fromMilliSecondsToSeconds(entity.getExpiration());
    model.setExpiration(
        expirationSeconds == null
            ? 0
            : TimeAdapter.fromLongWithTimeInSecondsToIntegerWithTimeInSeconds(
                expirationSeconds - model.getTimestamp()));
    Integer count = entity.getCount();
    model.setCount(count == null ? 0 : count);
    Integer remainingCount = entity.getRemainingCount();
    model.setRemainingCount(remainingCount == null ? 0 : remainingCount);
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

  public Long getExpiration() {
    return this.expiration;
  }

  public void setExpiration(Long expiration) {
    this.expiration = expiration;
  }

  public boolean isUpdated() {
    return this.isUpdated;
  }

  public Long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Integer getCount() {
    return this.count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Integer getRemainingCount() {
    return this.remainingCount;
  }

  public void setRemainingCount(Integer remainingCount) {
    this.remainingCount = remainingCount;
  }

  public void setUpdated(boolean updated) {
    isUpdated = updated;
  }
}
