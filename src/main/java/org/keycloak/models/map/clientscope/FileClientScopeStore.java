package org.keycloak.models.map.clientscope;

import org.keycloak.models.map.storage.file.EntityIO;
import org.keycloak.models.map.storage.file.EntityStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileClientScopeStore {

    private static final String OBJECT_DIRECTORY = "client-scopes";

    public static FileClientScopeEntity read(String clientId, String realmId) {
        Path expectedPath = createFilePath(clientId, realmId);
        return EntityStore.get(expectedPath);
    }

    public static boolean exists(String scopeId, String realmId) {
        if (scopeId == null || realmId == null) {
            return false;
        }
        return Files.exists(createFilePath(scopeId, realmId));
    }

    public static List<FileClientScopeEntity> readAll() {
        return EntityStore.getAll(FileClientScopeEntity.class);
    }

    public static FileClientScopeEntity update(FileClientScopeEntity entity) {
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
        EntityStore.getAll(FileClientScopeEntity.class).stream()
                .filter(e -> realmId.equals(e.getRealmId()))
                .map(FileClientScopeEntity::getId)
                .forEach(clientId -> deleteById(clientId, realmId));
    }

    private static Path createFilePath(String clientScopeId, String realmId) {
        return EntityIO.getPathForIdAndParentPath(clientScopeId, EntityIO.getRootDirectory()
                .resolve(realmId)
                .resolve(OBJECT_DIRECTORY));
    }
}
