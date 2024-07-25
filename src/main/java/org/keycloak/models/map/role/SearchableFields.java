package org.keycloak.models.map.role;

import org.keycloak.models.RoleModel;
import org.keycloak.storage.SearchableModelField;

public class SearchableFields {
    public static final SearchableModelField<RoleModel> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<RoleModel> REALM_ID = new SearchableModelField<>("realmId", String.class);
    /**
     * If client role, ID of the client (not the clientId)
     */
    public static final SearchableModelField<RoleModel> CLIENT_ID = new SearchableModelField<>("clientId", String.class);
    public static final SearchableModelField<RoleModel> NAME = new SearchableModelField<>("name", String.class);
    public static final SearchableModelField<RoleModel> DESCRIPTION = new SearchableModelField<>("description", String.class);
    public static final SearchableModelField<RoleModel> COMPOSITE_ROLE = new SearchableModelField<>("compositeRoles", Boolean.class);
}