package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import org.keycloak.common.util.Time;

import java.util.*;

/**
 * Reminders for myself while developing:
 * - We are completely trashing entity interfaces, because snakeyaml has problems figuring out the concrete implementation given an interface
 * (it works for specifying the type for collections but not for single interface objects see: https://bitbucket.org/snakeyaml/snakeyaml/wiki/Documentation chapter: Type safe Collections)
 */

public class FileRealmEntity implements AbstractEntity, UpdatableEntity {

    private String id;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean isUpdated;
    private String name;
    private String displayName;
    private String displayNameHtml;
    private Boolean enabled;
    private Boolean registrationAllowed;
    private Boolean registrationEmailAsUsername;
    private Boolean verifyEmail;
    private Boolean resetPasswordAllowed;
    private Boolean loginWithEmailAllowed;
    private Boolean duplicateEmailsAllowed;
    private Boolean rememberMe;
    private Boolean editUsernameAllowed;
    private Boolean revokeRefreshToken;
    private Boolean adminEventsEnabled;
    private Boolean adminEventsDetailsEnabled;
    private Boolean internationalizationEnabled;
    private Boolean allowUserManagedAccess;
    private Boolean offlineSessionMaxLifespanEnabled;
    private Boolean eventsEnabled;
    private Integer refreshTokenMaxReuse;
    private Integer ssoSessionIdleTimeout;
    private Integer ssoSessionMaxLifespan;
    private Integer ssoSessionIdleTimeoutRememberMe;
    private Integer ssoSessionMaxLifespanRememberMe;
    private Integer offlineSessionIdleTimeout;
    private Integer accessTokenLifespan;
    private Integer accessTokenLifespanForImplicitFlow;
    private Integer accessCodeLifespan;
    private Integer accessCodeLifespanUserAction;
    private Integer accessCodeLifespanLogin;
    private Long notBefore;
    private Integer clientSessionIdleTimeout;
    private Integer clientSessionMaxLifespan;
    private Integer clientOfflineSessionIdleTimeout;
    private Integer clientOfflineSessionMaxLifespan;
    private Integer actionTokenGeneratedByAdminLifespan;
    private Integer offlineSessionMaxLifespan;
    private Long eventsExpiration;
    private String passwordPolicy;
    private String sslRequired;
    private String loginTheme;
    private String accountTheme;
    private String adminTheme;
    private String emailTheme;
    private String masterAdminClient;
    private String defaultRoleId;
    private String defaultLocale;
    private String browserFlow;
    private String registrationFlow;
    private String directGrantFlow;
    private String resetCredentialsFlow;
    private String clientAuthenticationFlow;
    private String dockerAuthenticationFlow;
    private String firstBrokerLoginFlow;
    private FileOTPPolicyEntity otpPolicy;
    private FileWebAuthnPolicyEntity webAuthnPolicy;
    private FileWebAuthnPolicyEntity webAuthnPolicyPasswordless;
    private List<String> defaultClientScopeIds = new ArrayList<>();
    private List<String> optionalClientScopeIds = new ArrayList<>();
    private List<String> defaultGroupIds = new ArrayList<>();
    private List<String> eventsListeners = new ArrayList<>();
    private List<String> enabledEventTypes = new ArrayList<>();
    private List<String> supportedLocales = new ArrayList<>();
    private Map<String, Map<String, String>> localizationTexts = new HashMap<>();
    private Map<String, String> browserSecurityHeaders = new HashMap<>();
    private Map<String, String> smtpConfig = new HashMap<>();
    private List<FileRequiredCredentialEntity> requiredCredentials = new ArrayList<>();
    private List<FileComponentEntity> components = new ArrayList<>();
    private List<FileAuthenticationFlowEntity> authenticationFlows = new ArrayList<>();
    private List<FileAuthenticationExecutionEntity> authenticationExecutions = new ArrayList<>();
    private ArrayList<FileAuthenticatorConfigEntity> authenticatorConfigs = new ArrayList<>();
    private ArrayList<FileRequiredActionConfigEntity> requiredActionConfigs = new ArrayList<>();
    private ArrayList<FileRequiredActionProviderEntity> requiredActionProviders = new ArrayList<>();
    private ArrayList<FileIdentityProviderEntity> identityProviders = new ArrayList<>();
    private ArrayList<FileIdentityProviderMapperEntity> identityProviderMappers = new ArrayList<>();
    private ArrayList<FileClientInitialAccessEntity> clientInitialAccesses = new ArrayList<>();
    private boolean hasClientInitialAccess = false;
    private Boolean organizationsEnabled;


    public FileRealmEntity() {
    }


    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public void setId(String id) {
        if (this.id != null) throw new IllegalStateException("Id cannot be changed");
        this.id = id;
        this.isUpdated |= id != null;
    }

    public Map<String, List<String>> retrieveListAttributes() {
        Map<String, List<String>> atts = new HashMap<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            atts.put(key, List.of(value.toString()));
        }
        return atts;
    }


    public void modifyListAttributes(Map<String, List<String>> attributes) {
        this.attributes = new HashMap<>();
        for (String key : attributes.keySet()) {
            this.attributes.put(key, attributes.get(key));
        }
        FileRealmStore.update(this);
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public List<String> getAttribute(String name) {
        Object value = this.attributes.get(name);
        if (value == null) {
            return Collections.emptyList();
        }
        return List.of(value.toString());
    }


    public void setAttribute(String name, List<String> value) {
        assert value.size() == 1;
        this.attributes.put(name, value.get(0));
        FileRealmStore.update(this);
    }


    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }


    @Override
    public boolean isUpdated() {
        return this.isUpdated
                || Optional.ofNullable(getAuthenticationExecutions()).orElseGet(Collections::emptyList).stream().anyMatch(FileAuthenticationExecutionEntity::isUpdated)
                || Optional.ofNullable(getAuthenticationFlows()).orElseGet(Collections::emptyList).stream().anyMatch(FileAuthenticationFlowEntity::isUpdated)
                || Optional.ofNullable(getAuthenticatorConfigs()).orElseGet(Collections::emptyList).stream().anyMatch(FileAuthenticatorConfigEntity::isUpdated)
                || Optional.ofNullable(getClientInitialAccesses()).orElseGet(Collections::emptyList).stream().anyMatch(FileClientInitialAccessEntity::isUpdated)
                || Optional.ofNullable(getComponents()).orElseGet(Collections::emptyList).stream().anyMatch(FileComponentEntity::isUpdated)
                || Optional.ofNullable(getIdentityProviders()).orElseGet(Collections::emptyList).stream().anyMatch(FileIdentityProviderEntity::isUpdated)
                || Optional.ofNullable(getIdentityProviderMappers()).orElseGet(Collections::emptyList).stream().anyMatch(FileIdentityProviderMapperEntity::isUpdated)
                || Optional.ofNullable(getRequiredActionProviders()).orElseGet(Collections::emptyList).stream().anyMatch(FileRequiredActionProviderEntity::isUpdated)
                || Optional.ofNullable(getRequiredCredentials()).orElseGet(Collections::emptyList).stream().anyMatch(FileRequiredCredentialEntity::isUpdated)
                || Optional.ofNullable(getOtpPolicy()).map(FileOTPPolicyEntity::isUpdated).orElse(false)
                || Optional.ofNullable(getWebAuthnPolicy()).map(FileWebAuthnPolicyEntity::isUpdated).orElse(false)
                || Optional.ofNullable(getWebAuthnPolicyPasswordless()).map(FileWebAuthnPolicyEntity::isUpdated).orElse(false);
    }

    @Override
    public void clearUpdatedFlag() {
        this.isUpdated = false;
        Optional.ofNullable(getAuthenticationExecutions()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getAuthenticationFlows()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getAuthenticatorConfigs()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getClientInitialAccesses()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getComponents()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getIdentityProviders()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getIdentityProviderMappers()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getRequiredActionProviders()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getRequiredCredentials()).orElseGet(Collections::emptyList).forEach(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getOtpPolicy()).ifPresent(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getWebAuthnPolicy()).ifPresent(UpdatableEntity::clearUpdatedFlag);
        Optional.ofNullable(getWebAuthnPolicyPasswordless()).ifPresent(UpdatableEntity::clearUpdatedFlag);
    }

    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
        FileRealmStore.update(this);
    }


    public String getDisplayName() {
        return this.displayName;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        FileRealmStore.update(this);
    }


    public String getDisplayNameHtml() {
        return this.displayNameHtml;
    }


    public void setDisplayNameHtml(String displayNameHtml) {
        this.displayNameHtml = displayNameHtml;
        FileRealmStore.update(this);
    }


    public Boolean isEnabled() {
        return this.enabled;
    }


    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
        FileRealmStore.update(this);
    }


    public Boolean isRegistrationAllowed() {
        return this.registrationAllowed;
    }


    public void setRegistrationAllowed(Boolean registrationAllowed) {
        this.registrationAllowed = registrationAllowed;
        FileRealmStore.update(this);
    }


    public Boolean isRegistrationEmailAsUsername() {
        return this.registrationEmailAsUsername;
    }


    public void setRegistrationEmailAsUsername(Boolean registrationEmailAsUsername) {
        this.registrationEmailAsUsername = registrationEmailAsUsername;
        FileRealmStore.update(this);
    }


    public Boolean isVerifyEmail() {
        return this.verifyEmail;
    }


    public void setVerifyEmail(Boolean verifyEmail) {
        this.verifyEmail = verifyEmail;
        FileRealmStore.update(this);
    }


    public Boolean isResetPasswordAllowed() {
        return this.resetPasswordAllowed;
    }


    public void setResetPasswordAllowed(Boolean resetPasswordAllowed) {
        this.resetPasswordAllowed = resetPasswordAllowed;
        FileRealmStore.update(this);
    }


    public Boolean isLoginWithEmailAllowed() {
        return this.loginWithEmailAllowed;
    }


    public void setLoginWithEmailAllowed(Boolean loginWithEmailAllowed) {
        this.loginWithEmailAllowed = loginWithEmailAllowed;
        FileRealmStore.update(this);
    }


    public Boolean isDuplicateEmailsAllowed() {
        return this.duplicateEmailsAllowed;
    }


    public void setDuplicateEmailsAllowed(Boolean duplicateEmailsAllowed) {
        this.duplicateEmailsAllowed = duplicateEmailsAllowed;
        FileRealmStore.update(this);
    }


    public Boolean isRememberMe() {
        return this.rememberMe;
    }


    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
        FileRealmStore.update(this);
    }


    public Boolean isEditUsernameAllowed() {
        return this.editUsernameAllowed;
    }


    public void setEditUsernameAllowed(Boolean editUsernameAllowed) {
        this.editUsernameAllowed = editUsernameAllowed;
        FileRealmStore.update(this);
    }


    public Boolean isRevokeRefreshToken() {
        return this.revokeRefreshToken;
    }


    public void setRevokeRefreshToken(Boolean revokeRefreshToken) {
        this.revokeRefreshToken = revokeRefreshToken;
        FileRealmStore.update(this);
    }


    public Boolean isAdminEventsEnabled() {
        return this.adminEventsEnabled;
    }


    public void setAdminEventsEnabled(Boolean adminEventsEnabled) {
        this.adminEventsEnabled = adminEventsEnabled;
        FileRealmStore.update(this);
    }


    public Boolean isAdminEventsDetailsEnabled() {
        return adminEventsDetailsEnabled;
    }


    public void setAdminEventsDetailsEnabled(Boolean adminEventsDetailsEnabled) {
        this.adminEventsDetailsEnabled = adminEventsDetailsEnabled;
        FileRealmStore.update(this);
    }


    public Boolean isInternationalizationEnabled() {
        return this.internationalizationEnabled;
    }


    public void setInternationalizationEnabled(Boolean internationalizationEnabled) {
        this.internationalizationEnabled = internationalizationEnabled;
        FileRealmStore.update(this);
    }


    public Boolean isAllowUserManagedAccess() {
        return this.allowUserManagedAccess;
    }


    public void setAllowUserManagedAccess(Boolean allowUserManagedAccess) {
        this.allowUserManagedAccess = allowUserManagedAccess;
        FileRealmStore.update(this);
    }


    public Boolean isOfflineSessionMaxLifespanEnabled() {
        return this.offlineSessionMaxLifespanEnabled;
    }


    public void setOfflineSessionMaxLifespanEnabled(Boolean offlineSessionMaxLifespanEnabled) {
        this.offlineSessionMaxLifespanEnabled = offlineSessionMaxLifespanEnabled;
        FileRealmStore.update(this);
    }


    public Boolean isEventsEnabled() {
        return this.eventsEnabled;
    }


    public void setEventsEnabled(Boolean eventsEnabled) {
        this.eventsEnabled = eventsEnabled;
        FileRealmStore.update(this);
    }


    public Integer getRefreshTokenMaxReuse() {
        return this.refreshTokenMaxReuse;
    }


    public void setRefreshTokenMaxReuse(Integer refreshTokenMaxReuse) {
        this.refreshTokenMaxReuse = refreshTokenMaxReuse;
        FileRealmStore.update(this);
    }


    public Integer getSsoSessionIdleTimeout() {
        return this.ssoSessionIdleTimeout;
    }


    public void setSsoSessionIdleTimeout(Integer ssoSessionIdleTimeout) {
        this.ssoSessionIdleTimeout = ssoSessionIdleTimeout;
        FileRealmStore.update(this);
    }


    public Integer getSsoSessionMaxLifespan() {
        return ssoSessionMaxLifespan;
    }


    public void setSsoSessionMaxLifespan(Integer ssoSessionMaxLifespan) {
        this.ssoSessionMaxLifespan = ssoSessionMaxLifespan;
        FileRealmStore.update(this);
    }


    public Integer getSsoSessionIdleTimeoutRememberMe() {
        return this.ssoSessionIdleTimeoutRememberMe;
    }


    public void setSsoSessionIdleTimeoutRememberMe(Integer ssoSessionIdleTimeoutRememberMe) {
        this.ssoSessionIdleTimeoutRememberMe = ssoSessionIdleTimeoutRememberMe;
        FileRealmStore.update(this);
    }


    public Integer getSsoSessionMaxLifespanRememberMe() {
        return ssoSessionMaxLifespanRememberMe;
    }


    public void setSsoSessionMaxLifespanRememberMe(Integer ssoSessionMaxLifespanRememberMe) {
        this.ssoSessionMaxLifespanRememberMe = ssoSessionMaxLifespanRememberMe;
        FileRealmStore.update(this);
    }


    public Integer getOfflineSessionIdleTimeout() {
        return this.offlineSessionIdleTimeout;
    }


    public void setOfflineSessionIdleTimeout(Integer offlineSessionIdleTimeout) {
        this.offlineSessionIdleTimeout = offlineSessionIdleTimeout;
        FileRealmStore.update(this);
    }


    public Integer getAccessTokenLifespan() {
        return this.accessTokenLifespan;
    }


    public void setAccessTokenLifespan(Integer accessTokenLifespan) {
        this.accessTokenLifespan = accessTokenLifespan;
        FileRealmStore.update(this);
    }


    public Integer getAccessTokenLifespanForImplicitFlow() {
        return this.accessTokenLifespanForImplicitFlow;
    }


    public void setAccessTokenLifespanForImplicitFlow(Integer accessTokenLifespanForImplicitFlow) {
        this.accessTokenLifespanForImplicitFlow = accessTokenLifespanForImplicitFlow;
        FileRealmStore.update(this);
    }


    public Integer getAccessCodeLifespan() {
        return this.accessCodeLifespan;
    }


    public void setAccessCodeLifespan(Integer accessCodeLifespan) {
        this.accessCodeLifespan = accessCodeLifespan;
        FileRealmStore.update(this);
    }


    public Integer getAccessCodeLifespanUserAction() {
        return this.accessCodeLifespanUserAction;
    }


    public void setAccessCodeLifespanUserAction(Integer accessCodeLifespanUserAction) {
        this.accessCodeLifespanUserAction = accessCodeLifespanUserAction;
        FileRealmStore.update(this);
    }


    public Integer getAccessCodeLifespanLogin() {
        return this.accessCodeLifespanLogin;
    }


    public void setAccessCodeLifespanLogin(Integer accessCodeLifespanLogin) {
        this.accessCodeLifespanLogin = accessCodeLifespanLogin;
        FileRealmStore.update(this);
    }


    public Long getNotBefore() {
        return this.notBefore;
    }


    public void setNotBefore(Long notBefore) {
        this.notBefore = notBefore;
        FileRealmStore.update(this);
    }


    public Integer getClientSessionIdleTimeout() {
        return this.clientSessionIdleTimeout;
    }


    public void setClientSessionIdleTimeout(Integer clientSessionIdleTimeout) {
        this.clientSessionIdleTimeout = clientSessionIdleTimeout;
        FileRealmStore.update(this);
    }


    public Integer getClientSessionMaxLifespan() {
        return this.clientSessionMaxLifespan;
    }


    public void setClientSessionMaxLifespan(Integer clientSessionMaxLifespan) {
        this.clientSessionMaxLifespan = clientSessionMaxLifespan;
        FileRealmStore.update(this);
    }


    public Integer getClientOfflineSessionIdleTimeout() {
        return this.clientOfflineSessionIdleTimeout;
    }


    public void setClientOfflineSessionIdleTimeout(Integer clientOfflineSessionIdleTimeout) {
        this.clientOfflineSessionIdleTimeout = clientOfflineSessionIdleTimeout;
        FileRealmStore.update(this);
    }


    public Integer getClientOfflineSessionMaxLifespan() {
        return this.clientOfflineSessionMaxLifespan;
    }


    public void setClientOfflineSessionMaxLifespan(Integer clientOfflineSessionMaxLifespan) {
        this.clientOfflineSessionMaxLifespan = clientOfflineSessionMaxLifespan;
        FileRealmStore.update(this);
    }


    public Integer getActionTokenGeneratedByAdminLifespan() {
        return this.actionTokenGeneratedByAdminLifespan;
    }


    public void setActionTokenGeneratedByAdminLifespan(Integer actionTokenGeneratedByAdminLifespan) {
        this.actionTokenGeneratedByAdminLifespan = actionTokenGeneratedByAdminLifespan;
        FileRealmStore.update(this);
    }


    public Integer getOfflineSessionMaxLifespan() {
        return this.offlineSessionMaxLifespan;
    }


    public void setOfflineSessionMaxLifespan(Integer offlineSessionMaxLifespan) {
        this.offlineSessionMaxLifespan = offlineSessionMaxLifespan;
        FileRealmStore.update(this);
    }


    public Long getEventsExpiration() {
        return this.eventsExpiration;
    }


    public void setEventsExpiration(Long eventsExpiration) {
        this.eventsExpiration = eventsExpiration;
        FileRealmStore.update(this);
    }


    public String getPasswordPolicy() {
        return this.passwordPolicy;
    }


    public void setPasswordPolicy(String passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
        FileRealmStore.update(this);
    }


    public String getSslRequired() {
        return this.sslRequired;
    }


    public void setSslRequired(String sslRequired) {
        this.sslRequired = sslRequired;
        FileRealmStore.update(this);
    }

    public String getLoginTheme() {
        return this.loginTheme;
    }


    public void setLoginTheme(String loginTheme) {
        this.loginTheme = loginTheme;
        FileRealmStore.update(this);
    }


    public String getAccountTheme() {
        return this.accountTheme;
    }


    public void setAccountTheme(String accountTheme) {
        this.accountTheme = accountTheme;
        FileRealmStore.update(this);
    }


    public String getAdminTheme() {
        return this.adminTheme;
    }


    public void setAdminTheme(String adminTheme) {
        this.adminTheme = adminTheme;
        FileRealmStore.update(this);
    }


    public String getEmailTheme() {
        return this.emailTheme;
    }


    public void setEmailTheme(String emailTheme) {
        this.emailTheme = emailTheme;
        FileRealmStore.update(this);
    }


    public String getMasterAdminClient() {
        return this.masterAdminClient;
    }


    public void setMasterAdminClient(String masterAdminClient) {
        this.masterAdminClient = masterAdminClient;
        FileRealmStore.update(this);
    }


    public String getDefaultRoleId() {
        return this.defaultRoleId;
    }


    public void setDefaultRoleId(String defaultRoleId) {
        this.defaultRoleId = defaultRoleId;
        FileRealmStore.update(this);
    }


    public String getDefaultLocale() {
        return this.defaultLocale;
    }


    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
        FileRealmStore.update(this);
    }


    public String getBrowserFlow() {
        return this.browserFlow;
    }


    public void setBrowserFlow(String browserFlow) {
        this.browserFlow = browserFlow;
        FileRealmStore.update(this);
    }


    public String getRegistrationFlow() {
        return this.registrationFlow;
    }


    public void setRegistrationFlow(String registrationFlow) {
        this.registrationFlow = registrationFlow;
        FileRealmStore.update(this);
    }


    public String getDirectGrantFlow() {
        return this.directGrantFlow;
    }


    public void setDirectGrantFlow(String directGrantFlow) {
        this.directGrantFlow = directGrantFlow;
        FileRealmStore.update(this);
    }


    public String getResetCredentialsFlow() {
        return this.resetCredentialsFlow;
    }


    public void setResetCredentialsFlow(String resetCredentialsFlow) {
        this.resetCredentialsFlow = resetCredentialsFlow;
        FileRealmStore.update(this);
    }


    public String getClientAuthenticationFlow() {
        return clientAuthenticationFlow;
    }


    public void setClientAuthenticationFlow(String clientAuthenticationFlow) {
        this.clientAuthenticationFlow = clientAuthenticationFlow;
        FileRealmStore.update(this);
    }


    public String getDockerAuthenticationFlow() {
        return this.dockerAuthenticationFlow;
    }


    public void setDockerAuthenticationFlow(String dockerAuthenticationFlow) {
        this.dockerAuthenticationFlow = dockerAuthenticationFlow;
        FileRealmStore.update(this);
    }


    public String getFirstBrokerLoginFlow() {
        return this.firstBrokerLoginFlow;
    }


    public void setFirstBrokerLoginFlow(String firstBrokerLoginFlow) {
        this.firstBrokerLoginFlow = firstBrokerLoginFlow;
        FileRealmStore.update(this);
    }


    public FileOTPPolicyEntity getOtpPolicy() {
        return this.otpPolicy;
    }


    public void setOtpPolicy(FileOTPPolicyEntity otpPolicy) {
        this.otpPolicy = otpPolicy;
        FileRealmStore.update(this);
    }


    public FileWebAuthnPolicyEntity getWebAuthnPolicy() {
        return this.webAuthnPolicy;
    }


    public void setWebAuthnPolicy(FileWebAuthnPolicyEntity webAuthnPolicy) {
        this.webAuthnPolicy = webAuthnPolicy;
        FileRealmStore.update(this);
    }


    public FileWebAuthnPolicyEntity getWebAuthnPolicyPasswordless() {
        return this.webAuthnPolicyPasswordless;
    }


    public void setWebAuthnPolicyPasswordless(FileWebAuthnPolicyEntity webAuthnPolicyPasswordless) {
        this.webAuthnPolicyPasswordless = webAuthnPolicyPasswordless;
        FileRealmStore.update(this);
    }


    public List<String> getDefaultClientScopeIds() {
        return this.defaultClientScopeIds;
    }


    public void addDefaultClientScopeId(String scopeId) {
        this.defaultClientScopeIds.add(scopeId);
        FileRealmStore.update(this);
    }


    public Boolean removeDefaultClientScopeId(String scopeId) {
        Boolean hasRemoved = this.defaultClientScopeIds.remove(scopeId);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public List<String> getOptionalClientScopeIds() {
        return this.optionalClientScopeIds;
    }


    public void addOptionalClientScopeId(String scopeId) {
        this.optionalClientScopeIds.add(scopeId);
    }


    public Boolean removeOptionalClientScopeId(String scopeId) {
        return this.optionalClientScopeIds.remove(scopeId);
    }


    public List<String> getDefaultGroupIds() {
        return this.defaultGroupIds;
    }


    public void addDefaultGroupId(String groupId) {
        this.defaultGroupIds.add(groupId);
        FileRealmStore.update(this);
    }


    public void removeDefaultGroupId(String groupId) {
        this.defaultGroupIds.remove(groupId);
    }


    public List<String> getEventsListeners() {
        return this.eventsListeners;
    }


    public void setEventsListeners(List<String> eventsListeners) {
        this.eventsListeners = eventsListeners;
        FileRealmStore.update(this);
    }


    public List<String> getEnabledEventTypes() {
        return this.enabledEventTypes;
    }


    public void setEnabledEventTypes(List<String> enabledEventTypes) {
        this.enabledEventTypes = enabledEventTypes;
        FileRealmStore.update(this);
    }


    public List<String> getSupportedLocales() {
        return this.supportedLocales;
    }


    public void setSupportedLocales(List<String> supportedLocales) {
        this.supportedLocales = supportedLocales;
        FileRealmStore.update(this);
    }


    public Map<String, Map<String, String>> getLocalizationTexts() {
        return this.localizationTexts;
    }


    public Map<String, String> getLocalizationText(String locale) {
        return this.localizationTexts.get(locale);
    }


    public void setLocalizationText(String locale, Map<String, String> texts) {
        this.localizationTexts.put(locale, texts);
        FileRealmStore.update(this);
    }


    public Boolean removeLocalizationText(String locale) {
        Boolean hasRemoved = this.localizationTexts.remove(locale) != null;
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public Map<String, String> getBrowserSecurityHeaders() {
        return this.browserSecurityHeaders;
    }


    public void setBrowserSecurityHeaders(Map<String, String> headers) {
        this.browserSecurityHeaders = headers;
        FileRealmStore.update(this);
    }


    public void setBrowserSecurityHeader(String name, String value) {
        this.browserSecurityHeaders.put(name, value);
        FileRealmStore.update(this);
    }


    public Map<String, String> getSmtpConfig() {
        return this.smtpConfig;
    }


    public void setSmtpConfig(Map<String, String> smtpConfig) {
        this.smtpConfig = smtpConfig;
        FileRealmStore.update(this);
    }


    public List<FileRequiredCredentialEntity> getRequiredCredentials() {
        return this.requiredCredentials;
    }


    public void addRequiredCredential(FileRequiredCredentialEntity requiredCredential) {
        this.requiredCredentials.add(requiredCredential);
        FileRealmStore.update(this);
    }


    public List<FileComponentEntity> getComponents() {
        return this.components;
    }


    public Optional<FileComponentEntity> getComponent(String id) {
        return this.components.stream().filter(c -> c.getId().equals(id)).findFirst();
    }


    public void addComponent(FileComponentEntity component) {
        this.components.add(component);
        FileRealmStore.update(this);
    }


    public Boolean removeComponent(String componentId) {
        Boolean hasRemoved = this.components.remove(componentId);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public List<FileAuthenticationFlowEntity> getAuthenticationFlows() {
        return this.authenticationFlows;
    }


    public Optional<FileAuthenticationFlowEntity> getAuthenticationFlow(String flowId) {
        return this.authenticationFlows.stream().filter(f -> f.getId().equals(flowId)).findFirst();
    }


    public void addAuthenticationFlow(FileAuthenticationFlowEntity authenticationFlow) {
        this.authenticationFlows.add(authenticationFlow);
        FileRealmStore.update(this);
    }


    public Boolean removeAuthenticationFlow(String flowId) {
        Boolean hasRemoved = this.authenticationFlows.stream()
                .filter(f -> f.getId().equals(flowId))
                .findFirst()
                .map(f -> this.authenticationFlows.remove(f))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public List<FileAuthenticationExecutionEntity> getAuthenticationExecutions() {
        return this.authenticationExecutions;
    }

    public void setAuthenticationExecutions(List<FileAuthenticationExecutionEntity> authenticationExecutions) {
        this.authenticationExecutions = authenticationExecutions;
        FileRealmStore.update(this);
    }


    public Optional<FileAuthenticationExecutionEntity> getAuthenticationExecution(String id) {
        return this.authenticationExecutions.stream().filter(e -> e.getId().equals(id)).findFirst();
    }


    public void addAuthenticationExecution(FileAuthenticationExecutionEntity authenticationExecution) {
        this.authenticationExecutions.add(authenticationExecution);
        FileRealmStore.update(this);
    }


    public Boolean removeAuthenticationExecution(String executionId) {
        Boolean hasRemoved = this.authenticationExecutions.stream()
                .filter(e -> e.getId().equals(executionId))
                .findFirst()
                .map(e -> this.authenticationExecutions.remove(e))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public List<FileAuthenticatorConfigEntity> getAuthenticatorConfigs() {
        return this.authenticatorConfigs;
    }

    public List<FileRequiredActionConfigEntity> getRequiredActionConfigs() {
        return this.requiredActionConfigs;
    }


    public void addAuthenticatorConfig(FileAuthenticatorConfigEntity authenticatorConfig) {
        this.authenticatorConfigs.add(authenticatorConfig);
        FileRealmStore.update(this);
    }


    public Optional<FileAuthenticatorConfigEntity> getAuthenticatorConfig(String authenticatorConfigId) {
        return this.authenticatorConfigs.stream().filter(c -> c.getId().equals(authenticatorConfigId)).findFirst();
    }

    public Optional<FileRequiredActionConfigEntity> getRequiredActionConfig(String id) {
        return this.requiredActionConfigs.stream().filter(c -> c.getId().equals(id)).findFirst();
    }


    public Boolean removeAuthenticatorConfig(String authenticatorConfigId) {
        Boolean hasRemoved = this.authenticatorConfigs.stream()
                .filter(c -> c.getId().equals(authenticatorConfigId))
                .findFirst()
                .map(c -> this.authenticatorConfigs.remove(c))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }

    public Boolean removeRequiredActionConfig(String id) {
        Boolean hasRemoved = this.requiredActionConfigs.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(c -> this.requiredActionConfigs.remove(c))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public List<FileRequiredActionProviderEntity> getRequiredActionProviders() {
        return this.requiredActionProviders;
    }


    public void addRequiredActionProvider(FileRequiredActionProviderEntity requiredActionProvider) {
        this.requiredActionProviders.add(requiredActionProvider);
        FileRealmStore.update(this);
    }


    public Optional<FileRequiredActionProviderEntity> getRequiredActionProvider(String requiredActionProviderId) {
        return this.requiredActionProviders.stream().filter(p -> p.getId().equals(requiredActionProviderId)).findFirst();
    }


    public Boolean removeRequiredActionProvider(String requiredActionProviderId) {
        Boolean hasRemoved = this.requiredActionProviders.stream()
                .filter(p -> p.getId().equals(requiredActionProviderId))
                .findFirst()
                .map(p -> this.requiredActionProviders.remove(p))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public List<FileIdentityProviderEntity> getIdentityProviders() {
        return this.identityProviders;
    }


    public void addIdentityProvider(FileIdentityProviderEntity identityProvider) {
        this.identityProviders.add(identityProvider);
        FileRealmStore.update(this);
    }


    public Boolean removeIdentityProvider(String identityProviderId) {
        Boolean hasRemoved = this.identityProviders.stream()
                .filter(i -> i.getId().equals(identityProviderId))
                .findFirst()
                .map(i -> this.identityProviders.remove(i))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public List<FileIdentityProviderMapperEntity> getIdentityProviderMappers() {
        return this.identityProviderMappers;
    }


    public void addIdentityProviderMapper(FileIdentityProviderMapperEntity identityProviderMapper) {
        this.identityProviderMappers.add(identityProviderMapper);
        FileRealmStore.update(this);
    }


    public Boolean removeIdentityProviderMapper(String identityProviderMapperId) {
        Boolean hasRemoved = this.identityProviderMappers.stream()
                .filter(m -> m.getId().equals(identityProviderMapperId))
                .findFirst()
                .map(m -> this.identityProviderMappers.remove(m))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public Optional<FileIdentityProviderMapperEntity> getIdentityProviderMapper(String identityProviderMapperId) {
        return this.identityProviderMappers.stream().filter(m -> m.getId().equals(identityProviderMapperId)).findFirst();
    }


    public List<FileClientInitialAccessEntity> getClientInitialAccesses() {
        return this.clientInitialAccesses;
    }


    public void addClientInitialAccess(FileClientInitialAccessEntity clientInitialAccess) {
        this.clientInitialAccesses.add(clientInitialAccess);
        FileRealmStore.update(this);
    }


    public Optional<FileClientInitialAccessEntity> getClientInitialAccess(String clientInitialAccessId) {
        return this.clientInitialAccesses.stream().filter(c -> c.getId().equals(clientInitialAccessId)).findFirst();
    }


    public Boolean removeClientInitialAccess(String clientInitialAccessId) {
        Boolean hasRemoved = this.clientInitialAccesses.stream()
                .filter(c -> c.getId().equals(clientInitialAccessId))
                .findFirst()
                .map(c -> this.clientInitialAccesses.remove(c))
                .orElse(false);
        FileRealmStore.update(this);
        return hasRemoved;
    }


    public void removeExpiredClientInitialAccesses() {
        this.clientInitialAccesses.removeIf(e -> Time.currentTimeMillis() > e.getExpiration());
        FileRealmStore.update(this);
    }


    public boolean hasClientInitialAccess() {
        return this.hasClientInitialAccess;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        FileRealmStore.update(this);
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
        FileRealmStore.update(this);
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getRegistrationAllowed() {
        return registrationAllowed;
    }

    public Boolean getRegistrationEmailAsUsername() {
        return registrationEmailAsUsername;
    }

    public Boolean getVerifyEmail() {
        return verifyEmail;
    }

    public Boolean getResetPasswordAllowed() {
        return resetPasswordAllowed;
    }

    public Boolean getLoginWithEmailAllowed() {
        return loginWithEmailAllowed;
    }

    public Boolean getDuplicateEmailsAllowed() {
        return duplicateEmailsAllowed;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public Boolean getEditUsernameAllowed() {
        return editUsernameAllowed;
    }

    public Boolean getRevokeRefreshToken() {
        return revokeRefreshToken;
    }

    public Boolean getAdminEventsEnabled() {
        return adminEventsEnabled;
    }

    public Boolean getAdminEventsDetailsEnabled() {
        return adminEventsDetailsEnabled;
    }

    public Boolean getInternationalizationEnabled() {
        return internationalizationEnabled;
    }

    public Boolean getAllowUserManagedAccess() {
        return allowUserManagedAccess;
    }

    public Boolean getOfflineSessionMaxLifespanEnabled() {
        return offlineSessionMaxLifespanEnabled;
    }

    public Boolean getEventsEnabled() {
        return eventsEnabled;
    }

    public void setDefaultClientScopeIds(List<String> defaultClientScopeIds) {
        this.defaultClientScopeIds = defaultClientScopeIds;
        FileRealmStore.update(this);
    }

    public void setOptionalClientScopeIds(List<String> optionalClientScopeIds) {
        this.optionalClientScopeIds = optionalClientScopeIds;
        FileRealmStore.update(this);
    }

    public void setDefaultGroupIds(List<String> defaultGroupIds) {
        this.defaultGroupIds = defaultGroupIds;
        FileRealmStore.update(this);
    }

    public void setLocalizationTexts(Map<String, Map<String, String>> localizationTexts) {
        this.localizationTexts = localizationTexts;
        FileRealmStore.update(this);
    }

    public void setRequiredCredentials(List<FileRequiredCredentialEntity> requiredCredentials) {
        this.requiredCredentials = requiredCredentials;
        FileRealmStore.update(this);
    }

    public void setComponents(List<FileComponentEntity> components) {
        this.components = components;
        FileRealmStore.update(this);
    }

    public void setAuthenticationFlows(List<FileAuthenticationFlowEntity> authenticationFlows) {
        this.authenticationFlows = authenticationFlows;
        FileRealmStore.update(this);
    }

    public void setAuthenticatorConfigs(ArrayList<FileAuthenticatorConfigEntity> authenticatorConfigs) {
        this.authenticatorConfigs = authenticatorConfigs;
        FileRealmStore.update(this);
    }

    public void setRequiredActionProviders(ArrayList<FileRequiredActionProviderEntity> requiredActionProviders) {
        this.requiredActionProviders = requiredActionProviders;
        FileRealmStore.update(this);
    }

    public void setIdentityProviders(ArrayList<FileIdentityProviderEntity> identityProviders) {
        this.identityProviders = identityProviders;
        FileRealmStore.update(this);
    }

    public void setIdentityProviderMappers(ArrayList<FileIdentityProviderMapperEntity> identityProviderMappers) {
        this.identityProviderMappers = identityProviderMappers;
        FileRealmStore.update(this);
    }

    public void setClientInitialAccesses(ArrayList<FileClientInitialAccessEntity> clientInitialAccesses) {
        this.clientInitialAccesses = clientInitialAccesses;
        FileRealmStore.update(this);
    }

    public boolean isHasClientInitialAccess() {
        return hasClientInitialAccess;
    }

    public void setHasClientInitialAccess(boolean hasClientInitialAccess) {
        this.hasClientInitialAccess = hasClientInitialAccess;
        FileRealmStore.update(this);
    }

    public Boolean isOrganizationsEnabled() {
        return this.organizationsEnabled;
    }

    public void setOrganizationsEnabled(Boolean organizationsEnabled) {
        this.organizationsEnabled = organizationsEnabled;
        FileRealmStore.update(this);
    }
}
