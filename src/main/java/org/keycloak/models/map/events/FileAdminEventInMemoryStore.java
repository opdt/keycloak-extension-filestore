package org.keycloak.models.map.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileAdminEventInMemoryStore {

    private FileAdminEventInMemoryStore() {
    }

    private static final Map<String, FileAdminEventEntity> cache = new ConcurrentHashMap<>();

    public static boolean exists(String eventId) {
        return eventId != null && cache.containsKey(eventId);
    }

    public static List<FileAdminEventEntity> readAll() {
        return cache.values().stream().toList();
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
