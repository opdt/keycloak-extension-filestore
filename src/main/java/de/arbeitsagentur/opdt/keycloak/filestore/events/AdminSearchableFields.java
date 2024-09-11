package de.arbeitsagentur.opdt.keycloak.filestore.events;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

public class AdminSearchableFields {
  public static final SearchableModelField<AdminEvent> ID =
      new SearchableModelField<>("id", String.class);
  public static final SearchableModelField<AdminEvent> REALM_ID =
      new SearchableModelField<>("realmId", String.class);
  public static final SearchableModelField<AdminEvent> TIMESTAMP =
      new SearchableModelField<>("timestamp", Long.class);
  public static final SearchableModelField<AdminEvent> AUTH_REALM_ID =
      new SearchableModelField<>("authRealmId", String.class);
  public static final SearchableModelField<AdminEvent> AUTH_CLIENT_ID =
      new SearchableModelField<>("authClientId", String.class);
  public static final SearchableModelField<AdminEvent> AUTH_USER_ID =
      new SearchableModelField<>("authUserId", String.class);
  public static final SearchableModelField<AdminEvent> AUTH_IP_ADDRESS =
      new SearchableModelField<>("authIpAddress", String.class);
  public static final SearchableModelField<AdminEvent> OPERATION_TYPE =
      new SearchableModelField<>("operationType", OperationType.class);
  public static final SearchableModelField<AdminEvent> RESOURCE_TYPE =
      new SearchableModelField<>("resourceType", String.class);
  public static final SearchableModelField<AdminEvent> RESOURCE_PATH =
      new SearchableModelField<>("resourcePath", String.class);
}
