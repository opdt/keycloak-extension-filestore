package de.arbeitsagentur.opdt.keycloak.filestore.role;

import de.arbeitsagentur.opdt.keycloak.filestore.EntityIO;
import de.arbeitsagentur.opdt.keycloak.filestore.EntityStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileRoleStore {
  private static final String OBJECT_DIRECTORY = "roles";

  public static FileRoleEntity read(String clientId, String realmId) {
    Path expectedPath = createFilePath(clientId, realmId);
    return EntityStore.get(expectedPath);
  }

  public static boolean exists(String roleId, String realmId) {
    if (roleId == null || realmId == null) {
      return false;
    }
    return Files.exists(createFilePath(roleId, realmId));
  }

  public static List<FileRoleEntity> readAll() {
    return EntityStore.getAll(FileRoleEntity.class);
  }

  public static FileRoleEntity update(FileRoleEntity entity) {
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
    EntityStore.getAll(FileRoleEntity.class).stream()
        .filter(e -> realmId.equals(e.getRealmId()))
        .map(FileRoleEntity::getId)
        .forEach(clientId -> deleteById(clientId, realmId));
  }

  private static Path createFilePath(String roleId, String realmId) {
    return EntityIO.getPathForIdAndParentPath(
        roleId, EntityIO.getRootDirectory().resolve(realmId).resolve(OBJECT_DIRECTORY));
  }
}
