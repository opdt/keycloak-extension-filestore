package org.keycloak.models.map.realm;

import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.map.common.AbstractEntity;
import org.keycloak.models.map.common.UpdatableEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.util.HashMap;
import java.util.Map;

public class FileAuthenticatorConfigEntity implements AbstractEntity, UpdatableEntity {


    private String id;
    private boolean isUpdated;
    private String alias;
    private Map<String, String> config;


    static FileAuthenticatorConfigEntity fromModel(AuthenticatorConfigModel model) {
        if (model == null) return null;
        FileAuthenticatorConfigEntity entity = new FileAuthenticatorConfigEntity();
        String id = model.getId() == null ? KeycloakModelUtils.generateId() : model.getId();
        entity.setId(id);
        entity.setAlias(model.getAlias());
        entity.setConfig(model.getConfig());
        return entity;
    }

    static AuthenticatorConfigModel toModel(FileAuthenticatorConfigEntity entity) {
        if (entity == null) return null;
        AuthenticatorConfigModel model = new AuthenticatorConfigModel();
        model.setId(entity.getId());
        model.setAlias(entity.getAlias());
        Map<String, String> config = new HashMap<>();
        if (entity.getConfig() != null) config.putAll(entity.getConfig());
        model.setConfig(config);
        return model;
    }


    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public void setId(String id) {
        this.id = id;
    }


    public boolean isUpdated() {
        return this.isUpdated;
    }


    public String getAlias() {
        return this.alias;
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }


    public Map<String, String> getConfig() {
        return this.config;
    }


    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }
}
