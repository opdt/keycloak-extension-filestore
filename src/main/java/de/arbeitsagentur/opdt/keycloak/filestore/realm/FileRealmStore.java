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

package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import de.arbeitsagentur.opdt.keycloak.filestore.EntityIO;
import de.arbeitsagentur.opdt.keycloak.filestore.EntityStore;
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
