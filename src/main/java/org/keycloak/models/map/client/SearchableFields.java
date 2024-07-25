package org.keycloak.models.map.client;

import org.keycloak.models.ClientModel;
import org.keycloak.storage.SearchableModelField;

public class SearchableFields {
    public static final SearchableModelField<ClientModel> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<ClientModel> REALM_ID = new SearchableModelField<>("realmId", String.class);
    public static final SearchableModelField<ClientModel> CLIENT_ID = new SearchableModelField<>("clientId", String.class);
    public static final SearchableModelField<ClientModel> ENABLED = new SearchableModelField<>("enabled", Boolean.class);
    public static final SearchableModelField<ClientModel> SCOPE_MAPPING_ROLE = new SearchableModelField<>("scopeMappingRole", String.class);
    public static final SearchableModelField<ClientModel> ALWAYS_DISPLAY_IN_CONSOLE = new SearchableModelField<>("alwaysDisplayInConsole", Boolean.class);

    /**
     * Search for attribute value. The parameters is a pair {@code (attribute_name, value)} where {@code attribute_name}
     * is always checked for equality, and the value is checked per the operator.
     */
    public static final SearchableModelField<ClientModel> ATTRIBUTE = new SearchableModelField<>("attribute", String[].class);
}
