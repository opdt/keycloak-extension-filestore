package de.arbeitsagentur.opdt.keycloak.filestore.role;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileRoleEntity implements AbstractEntity, UpdatableEntity {

    private String id;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean isUpdated = false;
    private String realmId;
    private String clientId;
    private String name;
    private String description;
    private List<String> compositeRoles = new ArrayList<>();


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        FileRoleStore.update(this);
    }


    public Map<String, List<String>> getMultiValuedAttributes() {
        return this.attributes.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().toString()))
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), List.of(e.getValue())), HashMap::putAll);
    }


    public List<String> getAttribute(String name) {
        return List.of(this.attributes.get(name).toString());
    }


    public void setAttribute(String key, List<String> singleListValue) {
        this.attributes.put(key, singleListValue.get(0));
        FileRoleStore.update(this);
    }


    public void removeAttribute(String name) {
        this.attributes.remove(name);
        FileRoleStore.update(this);
    }


    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        this.isUpdated = updated;
        FileRoleStore.update(this);
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
        FileRoleStore.update(this);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
        FileRoleStore.update(this);
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
        FileRoleStore.update(this);
    }


    public List<String> getCompositeRoles() {
        return compositeRoles;
    }


    public void setCompositeRoles(List<String> compositeRoles) {
        this.compositeRoles = compositeRoles;
        FileRoleStore.update(this);
    }


    public void addCompositeRole(String roleId) {
        this.compositeRoles.add(roleId);
        FileRoleStore.update(this);
    }


    public void removeCompositeRole(String roleId) {
        this.compositeRoles.remove(roleId);
        FileRoleStore.update(this);
    }
}
