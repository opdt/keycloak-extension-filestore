package de.arbeitsagentur.opdt.keycloak.filestore.clientscope;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.models.ClientScopeModel;

public class SearchableFields {
  public static final SearchableModelField<ClientScopeModel> ID =
      new SearchableModelField<>("id", String.class);
  public static final SearchableModelField<ClientScopeModel> REALM_ID =
      new SearchableModelField<>("realmId", String.class);
  public static final SearchableModelField<ClientScopeModel> NAME =
      new SearchableModelField<>("name", String.class);
}
