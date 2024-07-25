package org.keycloak.models.map.client;

import org.keycloak.models.map.common.AbstractEntity;
import org.keycloak.models.map.common.UpdatableEntity;

import java.util.*;
import java.util.stream.Stream;

public class FileClientEntity implements AbstractEntity, UpdatableEntity {

    private Map<String, Boolean> clientScopes = new HashMap<>();
    private List<FileProtocolMapperEntity> protocolMappers = new ArrayList<>();
    private List<String> redirectUris = new ArrayList<>();
    private List<String> scopeMappings = new ArrayList<>();
    private List<String> webOrigins = new ArrayList<>();
    private Map<String, String> authenticationFlowBindingOverrides = new HashMap<>();
    private String baseUrl;
    private String clientAuthenticatorType;
    private String clientId;
    private String description;
    private String managementUrl;
    private String name;
    private Integer nodeReRegistrationTimeout;
    private Long notBefore;
    private String protocol;
    private String realmId;
    private String registrationToken;
    private String rootUrl;
    private List<String> scope = new ArrayList<>();
    private String secret;
    private Boolean alwaysDisplayInConsole;
    private Boolean bearerOnly;
    private Boolean consentRequired;
    private Boolean directAccessGrantsEnabled;
    private Boolean enabled;
    private Boolean frontchannelLogout;
    private Boolean fullScopeAllowed;
    private Boolean implicitFlowEnabled;
    private Boolean publicClient;
    private Boolean serviceAccountsEnabled;
    private Boolean standardFlowEnabled;
    private Boolean surrogateAuthRequired;
    private String id;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean updated;


    public Map<String, Boolean> getClientScopes() {
        return this.clientScopes;
    }


    public Stream<String> getClientScopes(boolean defaultScope) {
        final Map<String, Boolean> clientScopes = getClientScopes();
        return clientScopes == null ? Stream.empty() : clientScopes.entrySet().stream()
                .filter(me -> Objects.equals(me.getValue(), defaultScope))
                .map(Map.Entry::getKey);
    }


    public void setClientScope(String id, Boolean defaultScope) {
        this.clientScopes.put(id, defaultScope);
        FileClientStore.update(this);
    }


    public void removeClientScope(String id) {
        this.clientScopes.remove(id);
        FileClientStore.update(this);
    }


    public Optional<FileProtocolMapperEntity> getProtocolMapper(String id) {
        return this.protocolMappers.stream().filter(mapper -> mapper.getId().equals(id)).findFirst();
    }


    public List<FileProtocolMapperEntity> getProtocolMappers() {
        return this.protocolMappers;
    }

    public void addProtocolMapper(FileProtocolMapperEntity mapping) {
        this.protocolMappers.add(mapping);
        FileClientStore.update(this);
    }


    public void removeProtocolMapper(String id) {
        this.protocolMappers.removeIf(mapper -> mapper.getId().equals(id));
        FileClientStore.update(this);
    }

    public void addRedirectUri(String redirectUri) {
        this.redirectUris.add(redirectUri);
        FileClientStore.update(this);
    }


    public List<String> getRedirectUris() {
        return this.redirectUris;
    }


    public void removeRedirectUri(String redirectUri) {
        this.redirectUris.remove(redirectUri);
        FileClientStore.update(this);
    }


    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
        FileClientStore.update(this);
    }


    public void addScopeMapping(String id) {
        this.scopeMappings.add(id);
        FileClientStore.update(this);
    }


    public void removeScopeMapping(String id) {
        this.scopeMappings.remove(id);
        FileClientStore.update(this);
    }


    public List<String> getScopeMappings() {
        return this.scopeMappings;
    }

    public void addWebOrigin(String webOrigin) {
        this.webOrigins.add(webOrigin);
        FileClientStore.update(this);
    }


    public List<String> getWebOrigins() {
        return this.webOrigins;
    }


    public void removeWebOrigin(String webOrigin) {
        this.webOrigins.remove(webOrigin);
        FileClientStore.update(this);
    }


    public void setWebOrigins(List<String> webOrigins) {
        this.webOrigins = webOrigins;
        FileClientStore.update(this);
    }


    public String getAuthenticationFlowBindingOverride(String binding) {
        return this.authenticationFlowBindingOverrides.get(binding);
    }


    public Map<String, String> getAuthenticationFlowBindingOverrides() {
        return this.authenticationFlowBindingOverrides;
    }


    public void removeAuthenticationFlowBindingOverride(String binding) {
        this.authenticationFlowBindingOverrides.remove(binding);
        FileClientStore.update(this);
    }


    public void setAuthenticationFlowBindingOverride(String binding, String flowId) {
        this.authenticationFlowBindingOverrides.put(binding, flowId);
        FileClientStore.update(this);
    }


    public String getBaseUrl() {
        return this.baseUrl;
    }


    public String getClientAuthenticatorType() {
        return this.clientAuthenticatorType;
    }


    public String getClientId() {
        return this.clientId;
    }


    public String getDescription() {
        return this.description;
    }


    public String getManagementUrl() {
        return this.managementUrl;
    }


    public String getName() {
        return this.name;
    }


    public Integer getNodeReRegistrationTimeout() {
        return this.nodeReRegistrationTimeout;
    }


    public Long getNotBefore() {
        return this.notBefore;
    }


    public String getProtocol() {
        return this.protocol;
    }


    public String getRealmId() {
        return this.realmId;
    }

    public String getRegistrationToken() {
        return this.registrationToken;
    }


    public String getRootUrl() {
        return this.rootUrl;
    }


    public List<String> getScope() {
        return this.scope;
    }


    public String getSecret() {
        return this.secret;
    }


    public Boolean isAlwaysDisplayInConsole() {
        return this.alwaysDisplayInConsole;
    }


    public Boolean isBearerOnly() {
        return this.bearerOnly;
    }


    public Boolean isConsentRequired() {
        return this.consentRequired;
    }


    public Boolean isDirectAccessGrantsEnabled() {
        return this.directAccessGrantsEnabled;
    }


    public Boolean isEnabled() {
        return this.enabled;
    }


    public Boolean isFrontchannelLogout() {
        return this.frontchannelLogout;
    }


    public Boolean isFullScopeAllowed() {
        return this.fullScopeAllowed;
    }


    public Boolean isImplicitFlowEnabled() {
        return this.implicitFlowEnabled;
    }


    public Boolean isPublicClient() {
        return this.publicClient;
    }


    public Boolean isServiceAccountsEnabled() {
        return this.serviceAccountsEnabled;
    }


    public Boolean isStandardFlowEnabled() {
        return this.standardFlowEnabled;
    }


    public Boolean isSurrogateAuthRequired() {
        return this.surrogateAuthRequired;
    }


    public void setAlwaysDisplayInConsole(Boolean alwaysDisplayInConsole) {
        if (this.alwaysDisplayInConsole != alwaysDisplayInConsole) {
            this.alwaysDisplayInConsole = alwaysDisplayInConsole;
            FileClientStore.update(this);
        }
    }


    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        FileClientStore.update(this);
    }


    public void setBearerOnly(Boolean bearerOnly) {
        this.bearerOnly = bearerOnly;
        FileClientStore.update(this);
    }


    public void setClientAuthenticatorType(String clientAuthenticatorType) {
        this.clientAuthenticatorType = clientAuthenticatorType;
        FileClientStore.update(this);
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
        FileClientStore.update(this);
    }

    public void setConsentRequired(Boolean consentRequired) {
        if (this.consentRequired != consentRequired) {
            this.consentRequired = consentRequired;
            FileClientStore.update(this);
        }
    }


    public void setDescription(String description) {
        this.description = description;
        FileClientStore.update(this);
    }


    public void setDirectAccessGrantsEnabled(Boolean directAccessGrantsEnabled) {
        if (this.directAccessGrantsEnabled != directAccessGrantsEnabled) {
            this.directAccessGrantsEnabled = directAccessGrantsEnabled;
            FileClientStore.update(this);
        }
    }


    public void setEnabled(Boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            FileClientStore.update(this);
        }
    }


    public void setFrontchannelLogout(Boolean frontchannelLogout) {
        if (this.frontchannelLogout != frontchannelLogout) {
            this.frontchannelLogout = frontchannelLogout;
            FileClientStore.update(this);
        }
    }


    public void setFullScopeAllowed(Boolean fullScopeAllowed) {
        if (this.fullScopeAllowed != fullScopeAllowed) {
            this.fullScopeAllowed = fullScopeAllowed;
            FileClientStore.update(this);
        }
    }


    public void setImplicitFlowEnabled(Boolean implicitFlowEnabled) {
        if (this.implicitFlowEnabled != implicitFlowEnabled) {
            this.implicitFlowEnabled = implicitFlowEnabled;
            FileClientStore.update(this);
        }
    }


    public void setManagementUrl(String managementUrl) {
        this.managementUrl = managementUrl;
        FileClientStore.update(this);
    }


    public void setName(String name) {
        this.name = name;
        FileClientStore.update(this);
    }


    public void setNodeReRegistrationTimeout(Integer nodeReRegistrationTimeout) {
        this.nodeReRegistrationTimeout = nodeReRegistrationTimeout;
        FileClientStore.update(this);
    }


    public void setNotBefore(Long notBefore) {
        this.notBefore = notBefore;
        FileClientStore.update(this);
    }


    public void setProtocol(String protocol) {
        this.protocol = protocol;
        FileClientStore.update(this);
    }


    public void setPublicClient(Boolean publicClient) {
        if (this.publicClient != publicClient) {
            this.publicClient = publicClient;
            FileClientStore.update(this);
        }
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }


    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
        FileClientStore.update(this);
    }


    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
        FileClientStore.update(this);
    }


    public void setScope(List<String> scope) {
        this.scope = scope;
        FileClientStore.update(this);
    }


    public void setSecret(String secret) {
        this.secret = secret;
        FileClientStore.update(this);
    }


    public void setServiceAccountsEnabled(Boolean serviceAccountsEnabled) {
        if (this.serviceAccountsEnabled != serviceAccountsEnabled) {
            this.serviceAccountsEnabled = serviceAccountsEnabled;
            FileClientStore.update(this);
        }
    }


    public void setStandardFlowEnabled(Boolean standardFlowEnabled) {
        if (this.standardFlowEnabled != standardFlowEnabled) {
            this.standardFlowEnabled = standardFlowEnabled;
            FileClientStore.update(this);
        }
    }


    public void setSurrogateAuthRequired(Boolean surrogateAuthRequired) {
        if (this.surrogateAuthRequired != surrogateAuthRequired) {
            this.surrogateAuthRequired = surrogateAuthRequired;
            FileClientStore.update(this);
        }
    }


    public String getId() {
        return this.id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public Map<String, List<String>> getMultiValueAttributes() {
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
            result.put(entry.getKey(), List.of(entry.getValue().toString()));
        }
        return result;
    }


    public Map<String, Object> getAttributes() {
        return this.attributes;
    }


    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        FileClientStore.update(this);
    }


    public void setMultiValueAttributes(Map<String, List<String>> attributes) {
        for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
            this.attributes.put(entry.getKey(), entry.getValue().get(0));
        }
        FileClientStore.update(this);
    }


    public List<String> getAttribute(String name) {
        return Optional.ofNullable(this.attributes.get(name))
                .map(Object::toString)
                .map(List::of)
                .orElseGet(Collections::emptyList);
    }


    public void setAttribute(String name, List<String> value) {
        this.attributes.put(name, value.get(0));
        FileClientStore.update(this);
    }


    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }


    @Override
    public boolean isUpdated() {
        return this.updated
                || Optional.ofNullable(getProtocolMappers()).orElseGet(Collections::emptyList).stream().anyMatch(FileProtocolMapperEntity::isUpdated);
    }

    @Override
    public void clearUpdatedFlag() {
        this.updated = false;
        Optional.ofNullable(getProtocolMappers()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
    }

    public void setClientScopes(Map<String, Boolean> clientScopes) {
        this.clientScopes = clientScopes;
    }

    public void setProtocolMappers(List<FileProtocolMapperEntity> protocolMappers) {
        this.protocolMappers = protocolMappers;
    }

    public void setScopeMappings(List<String> scopeMappings) {
        this.scopeMappings = scopeMappings;
    }

    public void setAuthenticationFlowBindingOverrides(Map<String, String> authenticationFlowBindingOverrides) {
        this.authenticationFlowBindingOverrides = authenticationFlowBindingOverrides;
    }

    public Boolean getAlwaysDisplayInConsole() {
        return alwaysDisplayInConsole;
    }

    public Boolean getBearerOnly() {
        return bearerOnly;
    }

    public Boolean getConsentRequired() {
        return consentRequired;
    }

    public Boolean getDirectAccessGrantsEnabled() {
        return directAccessGrantsEnabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getFrontchannelLogout() {
        return frontchannelLogout;
    }

    public Boolean getFullScopeAllowed() {
        return fullScopeAllowed;
    }

    public Boolean getImplicitFlowEnabled() {
        return implicitFlowEnabled;
    }

    public Boolean getPublicClient() {
        return publicClient;
    }

    public Boolean getServiceAccountsEnabled() {
        return serviceAccountsEnabled;
    }

    public Boolean getStandardFlowEnabled() {
        return standardFlowEnabled;
    }

    public Boolean getSurrogateAuthRequired() {
        return surrogateAuthRequired;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
