package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FileIdentityProviderEntity implements AbstractEntity, UpdatableEntity {


    private String id;
    private boolean isUpdated = false;
    private String alias;
    private String displayName;
    private String providerId;
    private String firstBrokerLoginFlowId;
    private String postBrokerLoginFlowId;
    private Boolean enabled = false;
    private Boolean trustEmail = false;
    private Boolean storeToken = false;
    private Boolean linkOnly = false;
    private Boolean addReadTokenRoleOnCreate = false;
    private Boolean authenticateByDefault = false;
    private Map<String, String> config;


    static FileIdentityProviderEntity fromModel(IdentityProviderModel model) {
        if (model == null) return null;
        FileIdentityProviderEntity entity = new FileIdentityProviderEntity();
        String id = model.getInternalId() == null ? KeycloakModelUtils.generateId() : model.getInternalId();
        entity.setId(id);
        entity.setAlias(model.getAlias());
        entity.setDisplayName(model.getDisplayName());
        entity.setProviderId(model.getProviderId());
        entity.setFirstBrokerLoginFlowId(model.getFirstBrokerLoginFlowId());
        entity.setPostBrokerLoginFlowId(model.getPostBrokerLoginFlowId());
        entity.setEnabled(model.isEnabled());
        entity.setTrustEmail(model.isTrustEmail());
        entity.setStoreToken(model.isStoreToken());
        entity.setLinkOnly(model.isLinkOnly());
        entity.setAddReadTokenRoleOnCreate(model.isAddReadTokenRoleOnCreate());
        entity.setAuthenticateByDefault(model.isAuthenticateByDefault());
        entity.setConfig(model.getConfig());
        return entity;
    }

    static IdentityProviderModel toModel(FileIdentityProviderEntity entity, Supplier<IdentityProviderModel> instanceCreator) {
        if (entity == null) return null;
        IdentityProviderModel model = instanceCreator.get();
        model.setInternalId(entity.getId());
        model.setAlias(entity.getAlias());
        model.setDisplayName(entity.getDisplayName());
        model.setProviderId(entity.getProviderId());
        model.setFirstBrokerLoginFlowId(entity.getFirstBrokerLoginFlowId());
        model.setPostBrokerLoginFlowId(entity.getPostBrokerLoginFlowId());
        Boolean enabled = entity.isEnabled();
        model.setEnabled(enabled == null ? false : enabled);
        Boolean trustEmail = entity.isTrustEmail();
        model.setTrustEmail(trustEmail == null ? false : trustEmail);
        Boolean storeToken = entity.isStoreToken();
        model.setStoreToken(storeToken == null ? false : storeToken);
        Boolean linkOnly = entity.isLinkOnly();
        model.setLinkOnly(linkOnly == null ? false : linkOnly);
        Boolean addReadTokenRoleOnCreate = entity.isAddReadTokenRoleOnCreate();
        model.setAddReadTokenRoleOnCreate(addReadTokenRoleOnCreate == null ? false : addReadTokenRoleOnCreate);
        Boolean authenticateByDefault = entity.isAuthenticateByDefault();
        model.setAuthenticateByDefault(authenticateByDefault == null ? false : authenticateByDefault);
        Map<String, String> config = entity.getConfig();
        model.setConfig(config == null ? new HashMap<>() : new HashMap<>(config));
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


    public String getDisplayName() {
        return this.displayName;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getProviderId() {
        return this.providerId;
    }


    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }


    public String getFirstBrokerLoginFlowId() {
        return this.firstBrokerLoginFlowId;
    }


    public void setFirstBrokerLoginFlowId(String firstBrokerLoginFlowId) {
        this.firstBrokerLoginFlowId = firstBrokerLoginFlowId;
    }


    public String getPostBrokerLoginFlowId() {
        return this.postBrokerLoginFlowId;
    }


    public void setPostBrokerLoginFlowId(String postBrokerLoginFlowId) {
        this.postBrokerLoginFlowId = postBrokerLoginFlowId;
    }


    public Boolean isEnabled() {
        return this.enabled;
    }


    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    public Boolean isTrustEmail() {
        return this.trustEmail;
    }


    public void setTrustEmail(Boolean trustEmail) {
        this.trustEmail = trustEmail;
    }


    public Boolean isStoreToken() {
        return this.storeToken;
    }


    public void setStoreToken(Boolean storeToken) {
        this.storeToken = storeToken;
    }


    public Boolean isLinkOnly() {
        return this.linkOnly;
    }


    public void setLinkOnly(Boolean linkOnly) {
        this.linkOnly = linkOnly;
    }


    public Boolean isAddReadTokenRoleOnCreate() {
        return this.addReadTokenRoleOnCreate;
    }


    public void setAddReadTokenRoleOnCreate(Boolean addReadTokenRoleOnCreate) {
        this.addReadTokenRoleOnCreate = addReadTokenRoleOnCreate;
    }


    public Boolean isAuthenticateByDefault() {
        return this.authenticateByDefault;
    }


    public void setAuthenticateByDefault(Boolean authenticateByDefault) {
        this.authenticateByDefault = authenticateByDefault;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getTrustEmail() {
        return trustEmail;
    }

    public Boolean getStoreToken() {
        return storeToken;
    }

    public Boolean getLinkOnly() {
        return linkOnly;
    }

    public Boolean getAddReadTokenRoleOnCreate() {
        return addReadTokenRoleOnCreate;
    }

    public Boolean getAuthenticateByDefault() {
        return authenticateByDefault;
    }
}
