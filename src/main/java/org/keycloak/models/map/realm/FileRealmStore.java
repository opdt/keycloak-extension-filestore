package org.keycloak.models.map.realm;


import org.keycloak.models.map.storage.file.EntityIO;
import org.keycloak.models.map.storage.file.EntityStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileRealmStore {

    public static FileRealmEntity read(String realmId) {
        Path expectedPath = createFilePath(realmId);
        return EntityStore.get(expectedPath);
    }

    public static boolean exists(String realmId) {
        if (realmId == null) {
            return false;
        }
        return Files.exists(createFilePath(realmId));
    }

    public static List<FileRealmEntity> readAll() {
        return EntityStore.getAll(FileRealmEntity.class);
    }

    public static FileRealmEntity update(FileRealmEntity entity) {
        if (entity.getId() == null) {
            return null;
        }
        Path expectedPath = createFilePath(entity.getId());
        EntityStore.write(expectedPath, entity);
        return entity;
    }


    public static void deleteById(String realmId) {
        if (realmId != null) {
            Path expectedPath = createFilePath(realmId);
            EntityStore.delete(expectedPath);
        }
    }

    private static Path createFilePath(String realmId) {
        return EntityIO.getPathForIdAndParentPath(realmId, EntityIO.getRootDirectory());
    }
}
