package org.keycloak.models.map.events;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FileAuthEventInMemoryStore {

    private static final Map<String, FileAuthEventEntity> cache = new ConcurrentHashMap<>();

    public static boolean exists(String eventId) {
        return cache.containsKey(eventId);
    }

    public static List<FileAuthEventEntity> readAll() {
        return cache.values().stream()
                .filter(Objects::nonNull)
                .toList();
    }

    public static void create(FileAuthEventEntity entity) {
        cache.put(entity.getId(), entity);
    }

    public static void delete(FileAuthEventEntity entity) {
        cache.remove(entity.getId());
    }
}
