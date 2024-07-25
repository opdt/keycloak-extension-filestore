package org.keycloak.models.map.events;

import org.keycloak.events.EventType;
import org.keycloak.models.map.common.ExpirableEntity;

import java.util.Map;

public class FileAuthEventEntity implements ExpirableEntity {

    private String id;
    private Long expiration;
    private boolean updated;
    private Long timestamp;
    private EventType type;
    private String realmId;
    private String clientId;
    private String userId;
    private String sessionId;
    private String ipAddress;
    private String error;
    private Map<String, String> details;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public Long getExpiration() {
        return expiration;
    }


    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }


    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }


    public Long getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public EventType getType() {
        return type;
    }


    public void setType(EventType type) {
        this.type = type;
    }


    public String getRealmId() {
        return realmId;
    }


    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }


    public String getClientId() {
        return clientId;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public String getIpAddress() {
        return ipAddress;
    }


    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    public String getError() {
        return error;
    }


    public void setError(String error) {
        this.error = error;
    }


    public Map<String, String> getDetails() {
        return details;
    }


    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}
