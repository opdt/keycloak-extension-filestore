/*
 * Copyright 2024. IT-Systemhaus der Bundesagentur fuer Arbeit
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
