package org.keycloak.models.map.realm;

import org.keycloak.models.RequiredCredentialModel;
import org.keycloak.models.map.common.UpdatableEntity;

public class FileRequiredCredentialEntity implements UpdatableEntity {

    private boolean isUpdated = false;
    private String type;
    private String formLabel;
    private Boolean secret = false;
    private Boolean input = false;


    static FileRequiredCredentialEntity fromModel(RequiredCredentialModel model) {
        if (model == null) return null;
        FileRequiredCredentialEntity entity = new FileRequiredCredentialEntity();
        entity.setFormLabel(model.getFormLabel());
        entity.setType(model.getType());
        entity.setInput(model.isInput());
        entity.setSecret(model.isSecret());
        return entity;
    }

    static RequiredCredentialModel toModel(FileRequiredCredentialEntity entity) {
        if (entity == null) return null;
        RequiredCredentialModel model = new RequiredCredentialModel();
        model.setFormLabel(entity.getFormLabel());
        model.setType(entity.getType());
        Boolean secret = entity.isSecret();
        model.setSecret(secret == null ? false : secret);
        Boolean input = entity.isInput();
        model.setInput(input == null ? false : input);
        return model;
    }


    public boolean isUpdated() {
        return this.isUpdated;
    }


    public String getType() {
        return this.type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getFormLabel() {
        return this.formLabel;
    }


    public void setFormLabel(String formLabel) {
        this.formLabel = formLabel;
    }


    public Boolean isSecret() {
        return this.secret;
    }


    public void setSecret(Boolean secret) {
        this.secret = secret;
    }


    public Boolean isInput() {
        return this.input;
    }


    public void setInput(Boolean input) {
        this.input = input;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public Boolean getSecret() {
        return secret;
    }

    public Boolean getInput() {
        return input;
    }
}
