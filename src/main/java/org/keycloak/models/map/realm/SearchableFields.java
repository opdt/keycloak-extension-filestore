package org.keycloak.models.map.realm;

import org.keycloak.models.RealmModel;
import org.keycloak.storage.SearchableModelField;

public class SearchableFields {
    public static final SearchableModelField<RealmModel> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<RealmModel> NAME = new SearchableModelField<>("name", String.class);
    /**
     * Search for realms that have some client initial access set.
     */
    public static final SearchableModelField<RealmModel> CLIENT_INITIAL_ACCESS = new SearchableModelField<>("clientInitialAccess", Boolean.class);
    /**
     * Search for realms that have some component with
     */
    public static final SearchableModelField<RealmModel> COMPONENT_PROVIDER_TYPE = new SearchableModelField<>("componentProviderType", String.class);
}