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

package de.arbeitsagentur.opdt.keycloak.filestore.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileAdminEventInMemoryStore {

  private FileAdminEventInMemoryStore() {}

  private static final Map<String, FileAdminEventEntity> cache = new ConcurrentHashMap<>();

  public static boolean exists(String eventId) {
    return eventId != null && cache.containsKey(eventId);
  }

  public static List<FileAdminEventEntity> readAll() {
    return new ArrayList<>(cache.values());
  }

  public static void create(FileAdminEventEntity entity) {
    cache.put(entity.getId(), entity);
  }

  public static void delete(FileAdminEventEntity entity) {
    cache.remove(entity.getId());
  }

  public static void clearCache() {
    cache.clear();
  }
}
