package de.arbeitsagentur.opdt.keycloak.filestore.group;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileGroupEntity implements AbstractEntity, UpdatableEntity {

    private String id;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean isUpdated = false;
    private String name;
    private String parentId;
    private String realmId;
    private List<String> grantedRoles = new ArrayList<>();


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        FileGroupStore.update(this);
    }

    public void setAttribute(String key, List<String> singleListValue) {
        this.attributes.put(key, singleListValue.get(0));
        FileGroupStore.update(this);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
        FileGroupStore.update(this);
    }

    public Map<String, List<String>> getMultiValuedAttributes() {
        return attributes.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().toString()))
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), List.of(e.getValue())), HashMap::putAll);
    }

    public List<String> getAttribute(String name) {
        Object attr = this.attributes.get(name);
        return attr == null ? List.of() : List.of(attr.toString());
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
        FileGroupStore.update(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        FileGroupStore.update(this);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
        FileGroupStore.update(this);
    }

    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public List<String> getGrantedRoles() {
        return grantedRoles;
    }

    public void setGrantedRoles(List<String> grantedRoles) {
        this.grantedRoles = grantedRoles;
        FileGroupStore.update(this);
    }

    public void addGrantedRole(String role) {
        this.grantedRoles.add(role);
        FileGroupStore.update(this);
    }

    public void removeGrantedRole(String role) {
        this.grantedRoles.remove(role);
        FileGroupStore.update(this);
    }
}
