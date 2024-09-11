package de.arbeitsagentur.opdt.keycloak.filestore.clientscope;

import org.keycloak.models.ClientScopeModel;
import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;

public class SearchableFields {
    public static final SearchableModelField<ClientScopeModel> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<ClientScopeModel> REALM_ID = new SearchableModelField<>("realmId", String.class);
    public static final SearchableModelField<ClientScopeModel> NAME = new SearchableModelField<>("name", String.class);
}
