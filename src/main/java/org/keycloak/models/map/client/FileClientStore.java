package org.keycloak.models.map.client;

import org.keycloak.models.map.storage.file.EntityIO;
import org.keycloak.models.map.storage.file.EntityStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Note: The clientId is the filename, not the id.
 */
public class FileClientStore {

    private static final String OBJECT_DIRECTORY = "clients";

    public static FileClientEntity read(String clientId, String realmId) {
        Path expectedPath = createFilePath(clientId, realmId);
        return EntityStore.get(expectedPath);
    }

    public static boolean exists(String clientId, String realmId) {
        if (clientId == null || realmId == null) {
            return false;
        }
        return Files.exists(createFilePath(clientId, realmId));
    }

    public static List<FileClientEntity> readAll() {
        return EntityStore.getAll(FileClientEntity.class);
    }

    public static FileClientEntity update(FileClientEntity entity) {
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
        EntityStore.getAll(FileClientEntity.class).stream()
                .filter(e -> realmId.equals(e.getRealmId()))
                .map(FileClientEntity::getId)
                .forEach(clientId -> deleteById(clientId, realmId));
    }

    private static Path createFilePath(String groupId, String realmId) {
        return EntityIO.getPathForIdAndParentPath(groupId, EntityIO.getRootDirectory()
                .resolve(realmId)
                .resolve(OBJECT_DIRECTORY));
    }
}
