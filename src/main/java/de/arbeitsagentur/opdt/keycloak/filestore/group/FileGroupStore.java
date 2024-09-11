package de.arbeitsagentur.opdt.keycloak.filestore.group;

import de.arbeitsagentur.opdt.keycloak.filestore.EntityIO;
import de.arbeitsagentur.opdt.keycloak.filestore.EntityStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileGroupStore {
  private static final String OBJECT_DIRECTORY = "groups";

  public static FileGroupEntity read(String clientId, String realmId) {
    Path expectedPath = createFilePath(clientId, realmId);
    return EntityStore.get(expectedPath);
  }

  public static boolean exists(String groupId, String realmId) {
    if (groupId == null || realmId == null) {
      return false;
    }
    return Files.exists(createFilePath(groupId, realmId));
  }

  public static List<FileGroupEntity> readAll() {
    return EntityStore.getAll(FileGroupEntity.class);
  }

  public static FileGroupEntity update(FileGroupEntity entity) {
    if (entity.getId() == null || entity.getRealmId() == null) {
      return null;
    }
    Path expectedPath = createFilePath(entity.getId(), entity.getRealmId());
    EntityStore.write(expectedPath, entity);
    return entity;
  }

  public static void deleteById(String clientId, String realmId) {
    if (clientId != null && realmId != null) {
      Path expectedPath = createFilePath(clientId, realmId);
      EntityStore.delete(expectedPath);
    }
  }

  public static void deleteByRealmId(String realmId) {
    EntityStore.getAll(FileGroupEntity.class).stream()
        .filter(e -> realmId.equals(e.getRealmId()))
        .map(FileGroupEntity::getId)
        .forEach(clientId -> deleteById(clientId, realmId));
  }

  private static Path createFilePath(String groupId, String realmId) {
    return EntityIO.getPathForIdAndParentPath(
        groupId, EntityIO.getRootDirectory().resolve(realmId).resolve(OBJECT_DIRECTORY));
  }
}
