package org.keycloak.models.map.realm;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.map.common.AbstractEntity;
import org.keycloak.models.map.common.UpdatableEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

public class FileAuthenticationExecutionEntity implements AbstractEntity, UpdatableEntity {

    private String id;
    private boolean isUpdated = false;
    private String authenticator;
    private String authenticatorConfig;
    private AuthenticationExecutionModel.Requirement requirement;
    private Boolean autheticatorFlow = false;
    private String flowId;
    private String parentFlowId;
    private Integer priority;


    static FileAuthenticationExecutionEntity fromModel(AuthenticationExecutionModel model) {
        if (model == null) return null;
        FileAuthenticationExecutionEntity entity = new FileAuthenticationExecutionEntity();
        String id = model.getId() == null ? KeycloakModelUtils.generateId() : model.getId();
        entity.setId(id);
        entity.setAuthenticator(model.getAuthenticator());
        entity.setAuthenticatorConfig(model.getAuthenticatorConfig());
        entity.setFlowId(model.getFlowId());
        entity.setParentFlowId(model.getParentFlow());
        entity.setRequirement(model.getRequirement());
        entity.setAutheticatorFlow(model.isAuthenticatorFlow());
        entity.setPriority(model.getPriority());
        return entity;
    }

    static AuthenticationExecutionModel toModel(FileAuthenticationExecutionEntity entity) {
        if (entity == null) return null;
        AuthenticationExecutionModel model = new AuthenticationExecutionModel();
        model.setId(entity.getId());
        model.setAuthenticator(entity.getAuthenticator());
        model.setAuthenticatorConfig(entity.getAuthenticatorConfig());
        model.setFlowId(entity.getFlowId());
        model.setParentFlow(entity.getParentFlowId());
        model.setRequirement(entity.getRequirement());
        Boolean authenticatorFlow = entity.isAutheticatorFlow();
        model.setAuthenticatorFlow(authenticatorFlow == null ? false : authenticatorFlow);
        Integer priority = entity.getPriority();
        model.setPriority(priority == null ? 0 : priority);
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


    public String getAuthenticator() {
        return this.authenticator;
    }


    public void setAuthenticator(String authenticator) {
        this.authenticator = authenticator;
    }


    public String getAuthenticatorConfig() {
        return this.authenticatorConfig;
    }


    public void setAuthenticatorConfig(String authenticatorConfig) {
        this.authenticatorConfig = authenticatorConfig;
    }


    public AuthenticationExecutionModel.Requirement getRequirement() {
        return this.requirement;
    }


    public void setRequirement(AuthenticationExecutionModel.Requirement requirement) {
        this.requirement = requirement;
    }


    public Boolean isAutheticatorFlow() {
        return this.autheticatorFlow;
    }


    public void setAutheticatorFlow(Boolean autheticatorFlow) {
        this.autheticatorFlow = autheticatorFlow;
    }


    public String getFlowId() {
        return this.flowId;
    }


    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }


    public String getParentFlowId() {
        return this.parentFlowId;
    }


    public void setParentFlowId(String parentFlowId) {
        this.parentFlowId = parentFlowId;
    }


    public Integer getPriority() {
        return this.priority;
    }


    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public Boolean getAutheticatorFlow() {
        return autheticatorFlow;
    }
}
