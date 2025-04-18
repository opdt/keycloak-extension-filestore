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

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;

public class EventUtils {
    public static Event entityToModel(FileAuthEventEntity eventEntity) {
        Event event = new Event();
        event.setId(eventEntity.getId());
        event.setTime(eventEntity.getTimestamp());
        event.setType(eventEntity.getType());
        event.setRealmId(eventEntity.getRealmId());
        event.setClientId(eventEntity.getClientId());
        event.setUserId(eventEntity.getUserId());
        event.setSessionId(eventEntity.getSessionId());
        event.setIpAddress(eventEntity.getIpAddress());
        event.setError(eventEntity.getError());
        Map<String, String> details = eventEntity.getDetails();
        event.setDetails(details == null ? Collections.emptyMap() : details);
        return event;
    }

    public static AdminEvent entityToModel(FileAdminEventEntity adminEventEntity) {
        AdminEvent adminEvent = new AdminEvent();
        adminEvent.setId(adminEventEntity.getId());
        adminEvent.setTime(
                adminEventEntity.getTimestamp() != null
                        ? adminEventEntity.getTimestamp()
                        : Instant.now().toEpochMilli());
        adminEvent.setRealmId(adminEventEntity.getRealmId());
        setAuthDetails(adminEvent, adminEventEntity);
        adminEvent.setOperationType(adminEventEntity.getOperationType());
        adminEvent.setResourceTypeAsString(adminEventEntity.getResourceType());
        adminEvent.setResourcePath(adminEventEntity.getResourcePath());
        adminEvent.setError(adminEventEntity.getError());
        if (adminEventEntity.getRepresentation() != null) {
            adminEvent.setRepresentation(adminEventEntity.getRepresentation());
        }
        return adminEvent;
    }

    public static FileAdminEventEntity modelToEntity(AdminEvent adminEvent, boolean includeRepresentation) {
        FileAdminEventEntity mapAdminEvent = new FileAdminEventEntity();
        mapAdminEvent.setId(adminEvent.getId());
        mapAdminEvent.setTimestamp(adminEvent.getTime());
        mapAdminEvent.setRealmId(adminEvent.getRealmId());
        setAuthDetails(mapAdminEvent, adminEvent.getAuthDetails());
        mapAdminEvent.setOperationType(adminEvent.getOperationType());
        mapAdminEvent.setResourceType(adminEvent.getResourceTypeAsString());
        mapAdminEvent.setResourcePath(adminEvent.getResourcePath());
        mapAdminEvent.setError(adminEvent.getError());
        if (includeRepresentation) {
            mapAdminEvent.setRepresentation(adminEvent.getRepresentation());
        }
        return mapAdminEvent;
    }

    public static FileAuthEventEntity modelToEntity(Event event) {
        FileAuthEventEntity eventEntity = new FileAuthEventEntity();
        eventEntity.setId(event.getId());
        eventEntity.setTimestamp(event.getTime());
        eventEntity.setType(event.getType());
        eventEntity.setRealmId(event.getRealmId());
        eventEntity.setClientId(event.getClientId());
        eventEntity.setUserId(event.getUserId());
        eventEntity.setSessionId(event.getSessionId());
        eventEntity.setIpAddress(event.getIpAddress());
        eventEntity.setError(event.getError());
        eventEntity.setDetails(event.getDetails());
        return eventEntity;
    }

    private static void setAuthDetails(FileAdminEventEntity adminEventEntity, AuthDetails authDetails) {
        if (authDetails == null) return;
        adminEventEntity.setAuthRealmId(authDetails.getRealmId());
        adminEventEntity.setAuthClientId(authDetails.getClientId());
        adminEventEntity.setAuthUserId(authDetails.getUserId());
        adminEventEntity.setAuthIpAddress(authDetails.getIpAddress());
    }

    private static void setAuthDetails(AdminEvent adminEvent, FileAdminEventEntity adminEventEntity) {
        AuthDetails authDetails = new AuthDetails();
        authDetails.setRealmId(adminEventEntity.getAuthRealmId());
        authDetails.setClientId(adminEventEntity.getAuthClientId());
        authDetails.setUserId(adminEventEntity.getAuthUserId());
        authDetails.setIpAddress(adminEventEntity.getAuthIpAddress());
        adminEvent.setAuthDetails(authDetails);
    }
}
